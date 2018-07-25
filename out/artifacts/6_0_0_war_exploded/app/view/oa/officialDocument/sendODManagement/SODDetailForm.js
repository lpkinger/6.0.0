Ext.define('erp.view.oa.officialDocument.sendODManagement.SODDetailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpSODDetailFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'column'
    },
    fieldDefaults: {
    	columnWidth: .5
    },
    deleteUrl: 'oa/officialDocument/sendODManagement/deleteDraft.action',
	updateUrl: 'oa/officialDocument/sendODManagement/updateDraft.action',
	submitUrl: 'oa/officialDocument/sendODManagement/submitDraft.action',
	auditUrl: 'oa/officialDocument/sendODManagement/auditDraft.action',
	resSubmitUrl: 'oa/officialDocument/sendODManagement/resSubmitDraft.action',
	resAuditUrl: 'oa/officialDocument/sendODManagement/resAuditDraft.action',
    items: [{
        xtype: 'textfield',
        fieldLabel: '发文类型',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        id:'sod_type',
        name: 'sod_type'
    }, {
        xtype: 'textfield',
        fieldLabel: '发文日期',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        id:'sod_date',
        name: 'sod_date'
    }, {
        xtype: 'textfield',
        fieldLabel: '发文标题',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        id:'sod_title',
        name: 'sod_title'
    }, {
        xtype: 'textfield',
        fieldLabel: '发文机关',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        name: 'sod_fw_organ',
        id: 'sod_fw_organ'
    }, {
        xtype: 'textfield',
        fieldLabel: '拟稿人',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        name: 'sod_drafter',
        id: 'sod_drafter'
    }, {
    	 xtype: 'textfield',
         fieldLabel: '主题词',
         readOnly: true,
         fieldStyle: 'background:#f1f1f1;border:none;',
         name: 'sod_subject',
         id: 'sod_subject'
     }, {
    	 xtype: 'textfield',
         fieldLabel: '主送机关',
         readOnly: true,
         fieldStyle: 'background:#f1f1f1;border:none;',
         name: 'sod_zs_organ',
         id: 'sod_zs_organ'
     }, {
        xtype: 'textfield',
        fieldLabel: '秘密等级',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        name: 'sod_secretlevel',
        id: 'sod_secretlevel'
    }, {
        xtype: 'textfield',
        fieldLabel: '紧急程度',
        readOnly: true,
        fieldStyle: 'background:#f1f1f1;border:none;',
        name: 'sod_emergencydegree',
        id: 'sod_emergencydegree'
    }, {
    	 xtype: 'displayfield',
         fieldLabel: '附件',
         name: 'sod_attach',
         id: 'sod_attach'
    },{
        xtype: 'htmleditor',
        columnWidth: 1,
        hideLabel: true,
        autoScroll: true,
        height: height*0.8,
        name: 'sod_context',
        id: 'sod_context',
        anchor: '100%'//编辑框******
    },{
        xtype: 'textfield',
        hidden: true,
        id:'sod_id',
        name: 'sod_id'
    },{
        xtype: 'textfield',
        hidden: true,
        id:'sod_statuscode',
        name: 'sod_statuscode'
    }],
    tbar:[{
    	id: 'distribute',
    	text: '转收文',
    	iconCls: 'group-read',
    	cls: 'x-btn-gray'
    }],
    buttonAlign: 'center',
    statuscodeField: 'sod_statuscode',
	initComponent : function(){ 
		this.callParent(arguments);
		this.getSODDetail(getUrlParam('id'));
	},
	getSODDetail: function(id){
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/officialDocument/getSODDetail.action",
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
        		if(!res.sod){
        			return;
        		} else {
        			Ext.getCmp('sod_date').setValue(Ext.util.Format.date(new Date(res.sod.sod_date)));
        			Ext.getCmp('sod_type').setValue(res.sod.sod_type);
        			Ext.getCmp('sod_title').setValue(res.sod.sod_title);
        			Ext.getCmp('sod_id').setValue(res.sod.sod_id);
        			Ext.getCmp('sod_subject').setValue(res.sod.sod_subject);
        			Ext.getCmp('sod_zs_organ').setValue(res.sod.sod_zs_organ);
        			Ext.getCmp('sod_fw_organ').setValue(res.sod.sod_fw_organ);
        			Ext.getCmp('sod_secretlevel').setValue(res.sod.sod_secretlevel);
        			Ext.getCmp('sod_emergencydegree').setValue(res.sod.sod_emergencydegree);
        			Ext.getCmp('sod_context').setValue(res.sod.sod_context);
        			Ext.getCmp('sod_drafter').setValue(res.sod.sod_drafter);
        			Ext.getCmp('sod_statuscode').setValue(res.sod.sod_statuscode);
        			if(res.sod_attach != ""){
        				var attach = res.sod_attach.split(';');
        				var text = "";
        				Ext.each(attach, function(a, index){
        					var path = a.toString();
        					if(me.BaseUtil.contains(a, '\\', true)){
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('\\') + 1) + "</a>";
        					} else {
        						text += "&nbsp;&nbsp;<a class='mail-attach' href='" + basePath + "common/download.action?path=" + path + "'>" + a.substring(a.lastIndexOf('/') + 1) + "</a>";
        					}
        				});
        				Ext.getCmp('sod_attach').setValue(text);
        			} else {
        				Ext.getCmp('sod_attach').hide();
        			}
        		}
        		me.getForm().getFields().each(function (item,index,length){
    				item.originalValue = item.value;
    			});
        		me.addDocked({
        			xtype: 'toolbar',
        	        dock: 'bottom',
        			defaults: {
        				style: {
        					marginLeft: '14px'
        				}
        			},
        	        items: ['->',{
            	    	xtype: 'erpUpdateButton'
            	    },{
            	    	xtype: 'erpSubmitButton'
            	    },{
            	    	xtype: 'erpResSubmitButton'
            	    },{
            	    	xtype: 'erpAuditButton'
            	    },{
            	    	xtype: 'erpResAuditButton'
            	    },{
            	    	xtype: 'erpDeleteButton'
            	    },{
            	    	id: 'close',
            	    	text: '关闭',
            	    	iconCls: 'group-close',
            	    	cls: 'x-btn-gray'
            	    },'->']
        		});
        	}
        });
	}
});