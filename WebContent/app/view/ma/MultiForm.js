Ext.define('erp.view.ma.MultiForm',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'toolbar', 
				anchor: '100% 9%',
				margin:'0 0 5 0',
				cls:'x-formset-tbar',
				defaults:{
					margin:'0 5 0 0'
				},
				items: [{
					iconCls: 'x-button-icon-preview',
					name: 'FormBook',
					cls: 'x-btn-gray',
					text: $I18N.common.button.erpFormBookButton
				},{
					iconCls: 'x-button-icon-set',
					name: 'ReportFiles',
					cls: 'x-btn-gray',
					text: $I18N.common.button.erpReportFilesButton
				},{
					iconCls: 'x-button-icon-set',
					name: 'listSetting',
					cls: 'x-btn-gray',
					id:"listSetting",
					text: '列表设置'
				},{
					iconCls: 'x-button-icon-set',
					name: 'buttonGroupSet',
					cls: 'x-btn-gray',
					text: '按钮分组设置'
				},{
					iconCls: 'tree-save',
					name: 'save',
					cls: 'x-btn-gray',
					text: $I18N.common.button.erpSaveButton
				},{
					xtype: 'erpSyncButton',
					style: {
			    		marginLeft: '0'
			        }
				},{
					iconCls: 'x-button-icon-excel',
					text: '导出方案',
					cls: 'x-btn-gray',
					style: {
			    		marginLeft: '0'
			        },
			        handler: function() {
			        	var identity = Ext.JSON.encode({form: me.getFormParam(), grid: me.getGridParam()});
			        	window.open(basePath + 'common/dump/exp.action?type=Form&identity=' + encodeURIComponent(identity));
			        }
				},{
					iconCls: 'tree-delete',
					name: 'delete',
					cls: 'x-btn-gray',
					text: $I18N.common.button.erpDeleteButton
				},'->',{
					margin:0,
					iconCls: 'tree-close',
					name: 'close',
					cls: 'x-btn-gray',
					text: $I18N.common.button.erpCloseButton
				}]
			},{
				xtype: 'tabpanel', 
				anchor: '100% 91%',
				id: 'mytab',
				items: []
			}]
		}); 
		me.callParent(arguments);
		me.insertFormSet();
		me.insertGridSet();
	},
	getFormParam: function() {
		var me = this, whoami = getUrlParam('formParam'), cond = getUrlParam('formCondition');
		if(typeof whoami !== 'undefined' || typeof cond !== 'undefined') {
			if(cond) {
				cond = cond.replace('IS','=');// 兼容原写法
				whoami = cond.substr(cond.indexOf('=')+1);
			}
		}
		return 'null' == whoami ? null : whoami;
	},
	insertFormSet: function() {
		var me = this, whoami = me.getFormParam();
		if(whoami) {
			var formParams = whoami.split(','), tab = me.down('#mytab');
			Ext.Array.each(formParams, function(p, index){
				tab.add({
					title: '主表' + (formParams.length > 1 ? (index + 1) : ''),
					/*iconCls: 'formset-form',*/
					layout: 'anchor',
					items: [],
					dataId: p,
					listeners: {
						activate: function(panel) {
							me.getForm(panel);
						}
					}
				});
			});
			if(tab.items&&tab.items.items&&tab.items.items.length>0){
				tab.setActiveTab(0);//激活主表tab
			}
		}
	},
	getGridParam: function(){
		var me = this, whoami = getUrlParam('gridParam'), cal = getUrlParam('whoami');
		if(typeof whoami !== 'undefined') {
			cal && (whoami = cal);
		}
		return 'null' == whoami ? null : whoami;
	},
	insertGridSet: function() {
		var me = this, whoami = me.getGridParam();
		if(whoami) {
			var gridParams = whoami.split(','), tab = me.down('#mytab');
			Ext.Array.each(gridParams, function(p, index){
				tab.add({
					title: '从表' + (gridParams.length > 1 ? (index + 1) : ''),
					/*iconCls: 'formset-grid',*/
					layout: 'anchor',
					items: [],
					whoami: p,
					listeners: {
						activate: function(panel) {
							// 第一次activate的时候才加载
							me.getDetail(panel);
						}
					}
				});
			});
		}
	},
	getForm: function(panel) {
		if(!panel.firstReady) {
			panel.add([{
				xtype: 'myform',
				deleteUrl: 'ma/deleteMultiForm.action',
				updateUrl: 'ma/updateMultiForm.action',
				keyField: 'fo_id',
				anchor: '100% 45%',
				dataId: panel.dataId
			},{
				xtype: 'mygrid',
				anchor: '100% 55%',
				dataId: panel.dataId,
			    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1,
			        beforeEdit:function(context){
			        	if(context.record.data.fd_isfixed && (context.record.data.fd_isfixed==-1) && (context.field=='fd_table'||context.field=='fd_field'||context.field=='fd_logictype'||context.field=='deploy')) 
			        		return false;
			        	else return true;
			        }
			    })
			}]);
			panel.firstReady = true;
		}
	},
	getDetail: function(panel) {
		if(!panel.firstReady) {
			panel.add({
				xtype: 'mydetail',
				id: panel.id + '-grid',
				whoami: panel.whoami,
				anchor: '100% 100%',
				detno: 'dg_sequence',
				necessaryField: 'dg_field',
				keyField: 'dg_id',
			    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1,
			        beforeEdit:function(context){
			        	if(context.record.data.dg_isfixed && (context.record.data.dg_isfixed==-1) && (context.field=='dg_caller'||context.field=='dg_table'||context.field=='dg_field'||context.field=='dg_logictype'||context.field=='deploy')) 
			        		return false;
			        	else return true;
			        }
			    })
			});
			panel.firstReady = true;
		}
	}
});