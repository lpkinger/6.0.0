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
 <style type="text/css">
        body, h1, h2, h3, h4, h5, h6, hr, p, blockquote, dl, dt, dd, ul, ol, li, pre, form, fieldset, legend, button, input, textarea, th, td {
            margin: 0;
            padding: 0;
        }
        html,body{
            height:100%;
            width:100%;
            margin:0px;
        }

        h4{
            margin: 0 auto;
            width: 400px;
            text-align: left;
        }

        p{
            margin: 0 auto;
            width: 400px;
            text-align: left;
            line-height: 28px;
            font-family: Arial;
        }

        .head h3{
            margin:20px auto;
            text-align: center;
        }

        .head h3:before{
            width: 25px;
            height: 25px;
            content: url("<%=basePath %>resource/images/questions.png");
        }

        .first{
            height: 130px;
            margin:10px 0px 10px 0px;
        }
        .first ul{
            margin: 0 auto;
            width: 400px;
            text-align: left;
            list-style-type:none;
            
        }
        .first ul li:first-child{
            font-weight: bold;
        }
         .first ul li{
            margin:2px 0px;
            font-family: Arial;
        }
        .second{
            margin: 20px 0px 10px 0px;
        }
        .second h4{
            margin: 0 auto;
            width: 400px;
            text-align: left;
        }
        .second p{
            margin: 0 auto;
            width: 400px;
            text-align: left;
            line-height: 25px;
        }
		
		  .third {
            margin: 20px 0px 10px 0px;
        }
        .fourth {
            margin: 20px 0px;
        }
    </style>
</head>
<body>
    <div class="head">
        <h3>
          常见问题
        </h3>
    </div>
    <div class="first">
        <ul>
            <li>1、如何上传工资条?</li>
            <li>step1: 审批申请开通功能权限</li>
            <li>step2: 导出工资条模版</li>
            <li>step3: 上传制作好的工资条Excel表格</li>
            <li>step4: 核对数据,核对必填项和数据类型是否错误</li>
            <li>step5: 发送工资条</li>
        </ul>
    </div>
    <div class="second">
        <h4>
            2、上传工资条失败怎么办?
        </h4>
        <p>
            请查看工资条是否符合导出模版的格式，请按照需求定制自己的工资条模版
        </p>
    </div>
    <div class="third">
        <h4>
            3、员工如何查询到工资条信息?
        </h4>
        <p>
            只要开通UU互联账号，员工可以通过UU互联—企业工资条—输入查询密码
            即可查询到员工自己本人的工资信息
        </p>
    </div>
    <div class="fourth">
        <h4>
            4、上传工资信息数据安全吗?
        </h4>
        <p>
            工资信息只能通过本人授权人操作使用，授权人通过手机号绑定此功能模块的登录，
            数据只能授权人才能看到，查询着只能通过UU互联绑定手机号之后才能看到，
            数据通过HTTPS加密传输,服务器加密存储.
        </p>
    </div>
</body>
</html>