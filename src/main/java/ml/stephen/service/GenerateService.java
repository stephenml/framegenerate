/**
 * Frame Generate
 * https://github.com/stephenml/framegenerate
 *
 * The MIT License (MIT)
 *
 * Copyright © 2016 Stephen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ml.stephen.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GenerateService {

    /** 配置文件 */
    private static final String CONFIG_FILE = "config.json";
    /** 模板目录 */
    private static final String TEMPLATE_FOLDER = "template";
    /** 文件编码 */
    private static final String CHARSET = "UTF-8";
    /** 生成目录 */
    private static final String GENERATE_FOLDER = "generate";

    private static final String FILE_TYPE = "file";
    private static final String FOLDER_TYPE = "folder";
    private static final String PACKAGE_TYPE = "package";

    /** 项目目录 */
    private static String project_folder;
    /** 包目录 */
    private static String package_folder;
    /** license */
    private static String license;

    /** 项目名称 */
    private String projectName;
    /** 组目录 */
    private String groupFolder;

    public GenerateService(String projectName) throws Exception {
        System.out.println("创建项目 : " + projectName);

        /** 项目名称 */
        this.projectName = projectName;
        /** 项目目录 */
        project_folder = this.getProjectFolder();
        /** 创建项目目录 */
        this.createFolder(project_folder);
    }

    /**
     * 生成
     * @param params
     * @throws Exception
     */
    public void generate(Map<String, Object> params) throws Exception {
        JSONObject config = this.getConfig();

        /** 获取license */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/M/d");
        license = this.readFile((String) config.get("license"));
        license = license.replace("%|date|%", simpleDateFormat.format(new Date()));

        this.build(config, params, null);
    }

    /**
     * 构建
     * @param config
     * @param params
     * @param folder
     * @throws Exception
     */
    public void build(JSONObject config, Map<String, Object> params, String folder) throws Exception {
        /** 如果指定了目录 则项目目录则为这个目录 */
        if (null != folder) {
            project_folder = folder;
        }
        JSONArray array = this.getFilesConfig(config);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            this.buildObject(object, params, folder);
        }
    }

    /**
     * 根据类型构建
     * @param object
     * @param params
     * @param folder
     * @throws Exception
     */
    public void buildObject(JSONObject object, Map<String, Object> params, String folder) throws Exception {
        if (null == folder) {
            project_folder = this.getProjectFolder();
        } else {
            project_folder = folder;
        }

        String type = (String) object.get("type");

        if (type.equals(FILE_TYPE)) {
            /** 创建文件 */
            this.generateFile(object, params);
        } else if (type.equals(FOLDER_TYPE)) {
            /** 文件夹 */
            this.generateFolder(object, params, folder);
        } else if (type.equals(PACKAGE_TYPE)) {
            /** 包 */
            this.generatePackage(object, params, folder);
        }
    }

    /**
     * 生成文件
     * @param object
     * @param params
     * @throws Exception
     */
    public void generateFile(JSONObject object, Map<String, Object> params) throws Exception {
        /** 文件名 */
        String name = (String) object.get("name");
        /** 扩展名 */
        String extension = (String) object.get("extension");
        /** 模板文件 */
        String inFile = String.format("%s/%s", this.getTemplateFolder(), name);
        /** 输出文件 */
        String onFile = String.format("%s/%s", project_folder, name);
        if (!"".equals(extension)) {
            onFile = String.format("%s.%s", onFile, extension);
        }
        /** 生成文件 */
        this.buildFile(params, inFile, onFile, extension);

        System.out.println("创建文件 : " + onFile);
    }

    /**
     * 生成文件夹
     * @param object
     * @param params
     * @param folder
     * @throws Exception
     */
    public void generateFolder(JSONObject object, Map<String, Object> params, String folder) throws Exception {
        /** 文件夹名 */
        String name = (String) object.get("name");
        if (null == folder) {
            folder = String.format("%s/%s", project_folder, name);
        } else {
            folder = String.format("%s/%s", folder, name);
        }
        this.createFolder(folder);

        System.out.println("创建文件夹: " + folder);

        /** 创建子文件或文件夹 */
        this.build(object, params, folder);
    }

    /**
     * 生成包
     * @param object
     * @param params
     * @param folder
     * @throws Exception
     */
    public void generatePackage(JSONObject object, Map<String, Object> params, String folder) throws Exception {
        /** 包名 */
        String name = (String) object.get("name");
        if (name.equals("%|group|%")) {
            /** 组织包 */
            String packageName = (String) params.get("%|group|%");
            /** 包所在文件夹 */
            groupFolder = packageName.replace(".", "/");
            /** 分割包 */
            String[] packages = packageName.split("\\.");
            /** 创建包 */
            for (int i = 0; i < packages.length; i++) {
                if (null == folder) {
                    folder = String.format("%s/%s", project_folder, packages[i]);
                } else {
                    folder = String.format("%s/%s", folder, packages[i]);
                }
                this.createPackage(packages[i], folder);
            }
            package_folder = folder;

            /** 创建子包或子文件 */
            this.build(object, params, folder);
        } else {
            if (null == folder) {
                folder = package_folder;
            } else {
                folder = String.format("%s/%s", folder, name);
            }
            this.createPackage(name, folder);

            /** 创建子包或子文件 */
            this.build(object, params, folder);
        }
    }

    /**
     * 创建包
     * @param name
     * @param folder
     * @throws Exception
     */
    public void createPackage(String name, String folder) throws Exception {
        this.createFolder(folder);

        System.out.println("创建包: " + name + " >> " + folder);
    }

    /**
     * 构建文件
     * @param params
     * @param inFile
     * @param onFile
     * @param extension
     * @throws Exception
     */
    public void buildFile(Map<String, Object> params, String inFile, String onFile, String extension) throws Exception {
        String fileTxt = this.readFile(inFile);

        for (String key : params.keySet()) {
            fileTxt = fileTxt.replace(key, (String) params.get(key));
        }

        /** java文件追加license */
        if ("java".equals(extension)) {
            fileTxt = String.format("%s\r\n%s", license, fileTxt);
        }

        this.writeFile(fileTxt, onFile);
    }

    /**
     * 读取文件
     * @param inFile
     * @return
     * @throws Exception
     */
    public String readFile(String inFile) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(inFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, CHARSET);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            stringBuilder.append(lineTxt + "\r\n");
        }
        inputStreamReader.close();

        return stringBuilder.toString();
    }

    /**
     * 写入文件
     * @param fileTxt
     * @param onFile
     * @throws Exception
     */
    public void writeFile(String fileTxt, String onFile) throws Exception {
        File file = new File(onFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, CHARSET);
        outputStreamWriter.write(fileTxt);
        outputStreamWriter.flush();
    }

    /**
     * 创建目录
     * @param folder
     * @throws Exception
     */
    public void createFolder(String folder) throws Exception {
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获取项目目录
     * @return
     * @throws Exception
     */
    public String getProjectFolder() throws Exception {
        return String.format("%s/%s/%s", this.getDirectory(), GENERATE_FOLDER, this.projectName);
    }

    /**
     * 获取模板目录
     * @return
     * @throws Exception
     */
    public String getTemplateFolder() throws Exception {
        String folder = project_folder.replace(this.getProjectFolder(), "");
        if (null != groupFolder) {
            folder = folder.replace(groupFolder, "%|group|%");
        }
        return String.format("%s%s", TEMPLATE_FOLDER, folder);
    }

    /**
     * 获取生成根目录
     * @return
     * @throws Exception
     */
    public String getDirectory() throws Exception {
        File directory = new File(".");
        return directory.getCanonicalPath();
    }

    /**
     * 获取文件配置
     * @param config
     * @return
     * @throws Exception
     */
    public JSONArray getFilesConfig(JSONObject config) throws Exception {
        return (JSONArray) config.get("files");
    }

    /**
     * 获取配置文件json
     * @return
     * @throws Exception
     */
    public JSONObject getConfig() throws Exception {
        /** 读取配置文件 */
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONFIG_FILE);
        JSONParser jsonParser = new JSONParser();

        return (JSONObject) jsonParser.parse(new InputStreamReader(inputStream, CHARSET));
    }

}