<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath %>resource/ext/6.2/resources/theme-gray/resources/theme-gray-all.css">
<link rel="stylesheet" href="<%=basePath %>resource/css/codemirror.css">
<style type="text/css">
        .x-panel-body-default {
            background: #fff;
            border-color: #d0d0d0;
            color: #000;
            font-size: 14px;
            font-weight: normal;
            font-family: tahoma, arial, verdana, sans-serif;
            border-width: 1px;
            border-style: solid;
        }
        .cm-s-default .cm-keyword{
            color: #3764a0;
            font-weight: bold;
        }
        .execute-icon{
            background-image: url('<%=basePath %>/resource/images/install.png')
        }
        .export-icon{
            background-image: url('<%=basePath %>/resource/images/excel.png')
        }
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/6.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/codemirror.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/sql.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
        Ext.application({
            name: 'erp',
            appFolder: basePath+'app',
            launch: function() {
                Ext.create('erp.view.ma.sql.Viewport');//创建视图
            }
        });
    </script>
    <script type="text/javascript" src="<%=basePath %>resource/ext/6.2/exporter-debug.js"></script>
</head>
<body>
</body>
</html>