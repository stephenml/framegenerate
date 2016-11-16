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

package ml.stephen.entry;

import ml.stephen.service.GenerateService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainEntry {

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<String, Object>();
        Scanner scanner = new Scanner(System.in);

        System.out.print("组织名 : ");
        String group = scanner.next();
        params.put("%|group|%", group);

        System.out.print("项目名 : ");
        String name = scanner.next();
        params.put("%|name|%", name);

        System.out.print("版本号 : ");
        String version = scanner.next();
        params.put("%|version|%", version);

        /** 预设信息 */
        /** 开发环境 */
        params.put("%|developIP|%", "localhost");
        params.put("%|developProt|%", "80");
        params.put("%|developUpload|%", "/User/Stephen/Upload");
        params.put("%|developDatabaseIP|%", "localhost");
        params.put("%|developDatabaseProt|%", "3306");
        params.put("%|developDatabase|%", name);
        params.put("%|developDatabaseUser|%", "root");
        params.put("%|developDatabasePassword|%", "000000");
        params.put("%|developMongoDatabase|%", name);
        params.put("%|developLog|%", "/User/Stephen/Log");
        /** 发布环境 */
        params.put("%|releaseIP|%", "localhost");
        params.put("%|releaseUpload|%", "/usr/www/upload");
        params.put("%|releaseDatabaseIP|%", "localhost");
        params.put("%|releaseDatabaseProt|%", "3306");
        params.put("%|releaseDatabase|%", name);
        params.put("%|releaseDatabaseUser|%", "root");
        params.put("%|releaseDatabasePassword|%", "000000");
        params.put("%|releaseMongoDatabase|%", name);
        params.put("%|releaseLog|%", "/usr/www/log");

        try {
            Long startTime = System.currentTimeMillis();
            GenerateService generateService = new GenerateService(name);
            generateService.generate(params);
            Long endTime = System.currentTimeMillis();

            System.out.println("生成完成 耗时" + ((endTime - startTime) / 1000f) + "秒");
            System.out.println("生成项目所在文件夹 : " + generateService.getProjectFolder());
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("生成时发生错误 : " + e.getMessage());
        }
    }

}