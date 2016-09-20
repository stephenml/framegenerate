<a href="javascript:;" class="page-btn <%=pageNum == 1 ? 'disable' : 'prev-page'%>">
    &lt;
</a>
<span><em><%=pageNum %></em>/<%=totalPage %></span>
<a href="javascript:;" class="page-btn <%=(totalPage == 1 || pageNum == totalPage) ? 'disable' : 'next-page'%>">
    &gt;
</a>