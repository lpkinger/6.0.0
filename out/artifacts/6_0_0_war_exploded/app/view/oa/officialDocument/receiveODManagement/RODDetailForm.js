Ext.define('erp.view.oa.officialDocument.receiveODManagement.RODDetailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpRODDetailFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'displayfield',
        fieldLabel: 'ID',
        id:'rod_id',
        name: 'rod_id'
//    },{
//        xtype: 'displayfield',
//        fieldLabel: '审批状态',
//        id:'rod_status',
//        name: 'rod_status'
    },{
        xtype: 'displayfield',
        fieldLabel: '收文类型',
        id:'rod_type',
        name: 'rod_type'
    }, {
        xtype: 'displayfield',
        fieldLabel: '收文日期',
        id:'rod_date',
        name: 'rod_date'
    }, {
        xtype: 'displayfield',
        fieldLabel: '来文标题',
        id:'rod_title',
        name: 'rod_title'
    }, {
        xtype: 'displayfield',
        fieldLabel: '来文单位',
        name: 'rod_unit',
        id: 'rod_unit'
//    }, {
//        xtype: 'displayfield',
//        fieldLabel: '登记人',
//        name: 'rod_registrant',
//        id: 'rod_registrant'
    }, {
        xtype: 'displayfield',
        fieldLabel: '秘密等级',
        name: 'rod_secretlevel',
        id: 'rod_secretlevel'
    }, {
        xtype: 'displayfield',
        fieldLabel: '紧急程度',
        name: 'rod_emergencydegree',
        id: 'rod_emergencydegree'
    },{
        xtype: 'displayfield',
        fieldLabel: '主题词',
        name: 'rod_subject',
        id: 'rod_subject'
    }, {
    	 xtype: 'displayfield',
         fieldLabel: '附件',
         name: 'rod_attach',
         id: 'rod_attach'
    },{
        xtype: 'htmleditor',
        hideLabel: true,
        autoScroll: true,
        readOnly: true,
        height: height*0.8,
        name: 'rod_context',
        id: 'rod_context',
        anchor: '100%'//编辑框******
    }],
    tbar: [{
    	id: 'distribute',
    	text: '转发文',
    	iconCls: 'group-read',
    	cls: 'x-btn-gray'
    }],
    buttonAlign: 'center',
    buttons: [{
//    	id: 'file',
//    	text: '归档',
//    	iconCls: 'group-read',
//    	cls: 'x-btn-gray'
//    }, {
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getRODDetail(getUrlParam('id'));
	},
	getRODDetail: function(id){
//		if (id != null) {
//			id = id.substring(id.lastIndexOf("IS")+2);
//		}
//		alert(id);
		var me = this;
//		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/officialDocument/getRODDetail.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.rod){
        			return;
        		} else {
//        			var day = new Date(Ext.util.Format.date(new Date(res.rod.rod_date))); //将日期值格式化 
//        			var today = new Array("星期天","星期一","星期二","星期三","星期四","星期五","星期六"); 
        			Ext.getCmp('rod_date').setValue(Ext.util.Format.date(new Date(res.rod.rod_date)));// + "(" + today[day.getDay()] + ")");
        			Ext.getCmp('rod_type').setValue(res.rod.rod_type);
//        			Ext.getCmp('rod_status').hide().setValue(res.rod.rod_status);
        			Ext.getCmp('rod_id').hide().setValue(res.rod.rod_id);
        			Ext.getCmp('rod_title').setValue(res.rod.rod_title);
        			Ext.getCmp('rod_subject').setValue(res.rod.rod_subject);
        			Ext.getCmp('rod_unit').setValue(res.rod.rod_unit);
        			Ext.getCmp('rod_secretlevel').setValue(res.rod.rod_secretLevel);
        			Ext.getCmp('rod_emergencydegree').setValue(res.rod.rod_emergencyDegree);
        			Ext.getCmp('rod_context').setValue(res.rod.rod_context);
//        			Ext.getCmp('rod_registrant').setValue(res.rod_registrant);
        			if(res.rod_attach != ""){
        				var attach = res.rod_attach.split(';');
        				var text = "";
        				Ext.each(attach, function(a, index){
        					var path = a.toString();
        					if(me.BaseUtil.contains(a, '\\', true)){
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('\\') + 1) + "</a>";
        					} else {
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('/') + 1) + "</a>";
        					}
        				});
        				Ext.getCmp('rod_attach').setValue(text);
        			} else {
        				Ext.getCmp('rod_attach').hide();
        			}
        		}
        	}
        });
	}
});