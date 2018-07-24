Ext.define('erp.view.oa.myProcess.synergy.SeeSynergyForm',{ //文档查看界面
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpSeeSynergyFormPanel',
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
        fieldLabel: '标题',
        id:'sy_title',
        name: 'sy_title'
    }, {
        xtype: 'displayfield',
        fieldLabel: '协同类型',
        id:'sy_type',
        name: 'sy_type'
    }, {
        xtype: 'displayfield',
        fieldLabel: '发布日期',
        name: 'sy_date',
        id: 'sy_date'
    }, {
    	xtype: 'displayfield',
        fieldLabel: '发布人',
        name: 'sy_releaser',
        id: 'sy_releaser'
    }, {
       	xtype: 'displayfield',
        fieldLabel: '附件',
        name: 'attach',
        id: 'attach'
    }, {
       	xtype: 'htmleditor',
       	fieldLabel: '',
        height: 300,
        name: 'sy_content',
        readOnly: true,
        id: 'sy_content'
    }],
    buttonAlign: 'center',
    buttons: [{
//    	id: 'update',
//    	text: $I18N.common.button.erpUpdateButton,
//    	iconCls: 'x-button-icon-submit',
//    	cls: 'x-btn-gray'
//    },{
    	id: 'delete',
    	text: $I18N.common.button.erpDeleteButton,
    	iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray'
    },{
//    	id: 'export',
//    	text: $I18N.common.button.erpExportButton,
//    	iconCls: 'x-button-icon-submit',
//    	cls: 'x-btn-gray'
//    },{
    	id: 'print',
    	text: $I18N.common.button.erpPrintButton,
    	iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray'
    },{
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getSynergyDetail(getUrlParam('id'));
	},
	getSynergyDetail: function(id){
//		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/myProcess/synergy/getSynergy.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	callback : function(options, success, response){
//        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
//        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.synergy){
        			return;
        		} else {
        			Ext.getCmp('sy_title').setValue(res.synergy.sy_title);
        			Ext.getCmp('sy_content').setValue(res.synergy.sy_content);
        			Ext.getCmp('sy_type').setValue(res.synergy.sy_type);
        			Ext.getCmp('sy_releaser').setValue(res.synergy.sy_releaser);
//        			Ext.getCmp('attach').setValue(res.synergy.sy_attach_id);
        			if(res.synergy.sy_attach_id != null && res.synergy.sy_attach_id != ''){
        				var text = "";
        				var attach = res.synergy.sy_attach_id.split(',');
//        				var attach = new Array();
        				Ext.each(attach, function(a, index){
        					Ext.Ajax.request({//拿到grid的columns
        			        	url : basePath + "oa/myProcess/synergy/getAttach.action",
        			        	params: {
        			        		id: a
        			        	},
        			        	method : 'post',
        			        	async: false,
        			        	callback : function(options, success, response){
//        			        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
//        			        		console.log(response);
        			        		var res = new Ext.decode(response.responseText);
        			        		if(res.exceptionInfo){
        			        			showError(res.exceptionInfo);return;
        			        		}
        			        		if(res.success){
        			        			attach[index] = res.path;
//        			        			alert(attach[index]);
        			        		}
        			        	}
        					});
        				});
        				console.log(attach);
    					Ext.each(attach, function(a, index){
    						var path = a.toString();
    						if(me.BaseUtil.contains(a, '\\', true)){
    							text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('\\') + 1) + "</a>";
    						} else {
    							text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('/') + 1) + "</a>";
    						}
    					});	
    					alert(text);
        				Ext.getCmp('attach').setValue(text);
        			} else {
        				Ext.getCmp('attach').hide();
        			}
        			Ext.getCmp('sy_date').setValue(Ext.util.Format.date(new Date(res.synergy.sy_date),'Y-m-d H:i:s'));
        		}
        	}
        });
	}
});