Ext.define('erp.view.oa.mail.MailDetailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpMailDetailFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 55,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'displayfield',
        fieldLabel: '发件人',
        id:'ma_from',
        name: 'ma_from'
    }, {
        xtype: 'displayfield',
        fieldLabel: '时间',
        id:'ma_senddate',
        name: 'ma_senddate'
    }, {
        xtype: 'displayfield',
        fieldLabel: '收件人',
        id:'ma_to',
        name: 'ma_to'
    }, {
        xtype: 'displayfield',
        fieldLabel: '主题',
        name: 'ma_subject',
        id: 'ma_subject'
    }, {
    	 xtype: 'displayfield',
         fieldLabel: '附件',
         name: 'ma_attach',
         id: 'ma_attach'
    },{
        xtype: 'htmleditor',
        hideLabel: true,
        autoScroll: true,
        height: height*0.8,
        name: 'ma_context',
        id: 'ma_context',
        anchor: '100%'//编辑框******
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'post',
    	text: '回复',
    	iconCls: 'group-read',
    	cls: 'x-btn-gray'
    },{
    	id: 'save',
    	text: '删除',
    	iconCls: 'group-delete',
    	cls: 'x-btn-gray'
    },{
    	id: 'close',
    	text: '转发',
    	iconCls: 'group-post',
    	cls: 'x-btn-gray'
    }, {
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getMailDetail(getUrlParam('id'));
	},
	getMailDetail: function(id){
		var me = this;
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/mail/getMailDetail.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.mail){
        			return;
        		} else {
        			Ext.getCmp('ma_from').setValue(res.mail.ma_from + "&nbsp;&nbsp;<a class='mail-attach'>添加好友</a>"
        					+ "&nbsp;&nbsp;<a class='mail-attach'>查看往来邮件</a>");
        			var day = new Date(Date.parse(res.mail.ma_senddate.replace(/-/g, '/'))); //将日期值格式化 
        			var today = new Array("星期天","星期一","星期二","星期三","星期四","星期五","星期六"); 
        			Ext.getCmp('ma_senddate').setValue(res.mail.ma_senddate + "(" + today[day.getDay()] + ")");
        			Ext.getCmp('ma_to').setValue(res.mail.ma_receaddr);
        			Ext.getCmp('ma_subject').setValue(res.mail.ma_subject);
        			Ext.getCmp('ma_context').setValue(res.mail.ma_context);
        			if(res.mail.ma_attach != null){
        				var attach = res.mail.ma_attach.split(';');
        				var text = "";
        				Ext.each(attach, function(a, index){
        					var path = a.toString();
        					if(me.BaseUtil.contains(a, '\\', true)){
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('\\') + 1) + "</a>";
        					} else {
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('/') + 1) + "</a>";
        					}
        				});
        				Ext.getCmp('ma_attach').setValue(text);
        			} else {
        				Ext.getCmp('ma_attach').hide();
        			}
        		}
        	}
        });
	}
});