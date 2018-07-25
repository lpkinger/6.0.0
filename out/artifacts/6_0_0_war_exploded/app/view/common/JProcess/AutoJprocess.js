Ext.define('erp.view.common.JProcess.AutoJprocess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				
			}]
		}); 
		me.callParent(arguments); 
	},
	createTaskForm : function() {
		var me = this;
		return Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;border:none;',
			layout : 'column',
			anchor:'100% 100%',
			defaults : {
				columnWidth : 1/3,
				margin : '2 2 2 2'
			},
			items : [{
				xtype : 'textfield',
				name : 'ap_title',
				fieldLabel : '主题',
				allowBlank : false,
				fieldStyle:'border:true'
			},{
				xtype : 'textfield',
				name : 'ap_code',
				fieldLabel: '申请单号',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_kind',
				fieldLabel: '流程类别',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_feedbackman',
				fieldLabel: '实施反馈人',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_name',
				fieldLabel: '流程名称',
				readOnly : true
			},{
				xtype : 'datetimefield',
				name : 'ap_date',
				fieldLabel : '创建时间',
				value : new Date()
			},{
				xtype : 'textfield',
				name : 'ap_man',
				fieldLabel: '申请人',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_depart',
				fieldLabel: '申请部门',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_description',
				fieldLabel: '简要说明',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_readpersons',
				fieldLabel: '阅读人员',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_readjobs',
				fieldLabel: '阅读人员岗位',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_flowpersons',
				fieldLabel: '阅读审批人员',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_flowjobs',
				fieldLabel: '阅读审批人员岗位',
				readOnly : true
			},{
				xtype : 'mfilefield',
				name : 'ap_attach',
				fieldLabel : '附件信息',
				value : 24
			},{
				xtype : 'textfield',
				name : 'ap_keywords',
				fieldLabel: '知识关联',
				readOnly : true
			},{
				xtype : 'textfield',
				name : 'ap_caller',
				readOnly:true
			}],
			buttonAlign : 'center',
			buttons : [{
				text : '重置',
				cls : 'x-btn-blue',
				handler : function(b) {
					b.ownerCt.ownerCt.getForm().reset();
				}
			},{
				text : '确定',
				cls : 'x-btn-blue',
				formBind: true,
				handler : function(b) {
					me.onTaskAdd(b.ownerCt.ownerCt);
				}
			},{
				text : '关闭',
				cls : 'x-btn-blue',
				handler : function(b) {
					b.ownerCt.ownerCt.ownerCt.hide();
				}
			}]
		});
	}
});