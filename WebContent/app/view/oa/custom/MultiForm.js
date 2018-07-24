Ext.define('erp.view.oa.custom.MultiForm',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'tabpanel', 
					anchor: '100% 95%',
					id: 'mytab',
					items: [{
						title: '主表',
						id: 'maintab',
						iconCls: 'formset-form',
						layout: 'anchor',
						items: [{
							xtype: 'myform',
							saveUrl:'ma/saveMultiForm.action',
							deleteUrl: 'ma/deleteMultiForm.action',
							updateUrl: 'ma/updateMultiForm.action',
							getIdUrl: 'common/getId.action?seq=FORM_SEQ',
							keyField: 'fo_id',
							caller:caller,
							anchor: '100% 45%'
						},{
							xtype: 'customgrid',
							anchor: '100% 55%'
						}]
					},{
						title: '从表',
						id: 'detailtab',
						iconCls: 'formset-grid',
						layout: 'anchor',
						items: [{
							xtype: 'mydetail',
							anchor: '100% 100%',
							detno: 'dg_sequence',
							necessaryField: 'dg_field',
							keyField: 'dg_id',
							getGridColumnsAndStore: function(){
								var grid = this;
								var main = parent.Ext.getCmp("content-panel");
								if(!main)
									main = parent.parent.Ext.getCmp("content-panel");
								if(main){
									main.getActiveTab().setLoading(true);//loading...
								}
								Ext.Ajax.request({//拿到grid的columns
						        	url : basePath + 'common/singleGridPanel.action',
						        	async: false,
						        	params: {
						        		caller: grid.caller,
						        		condition: "dg_caller='" + whoami + "'"
						        	},
						        	method : 'post',
						        	callback : function(options,success,response){
						        		if(main){
						        			main.getActiveTab().setLoading(false);
						        		}
						        		var res = new Ext.decode(response.responseText);
						        		if(res.exceptionInfo){
						        			showError(res.exceptionInfo);return;
						        		}
						        		if(res.columns){
						        			grid.columns = res.columns;
						        			grid.fields = res.fields;
						        			grid.columns.push({
						        				xtype: 'checkcolumn',
						        				text: '配置',
						        				width: 60,
						        				dataIndex: 'deploy',
						        				cls: "x-grid-header-1",
						        				locked: true,
						        				editor: {
						        					xtype: 'checkbox',
						        					cls: "x-grid-checkheader-editor"
						        				}
						        			});
						        			grid.fields.push({name: 'deploy', type: 'bool'});
						        			//renderer
						        			grid.getRenderer();
						            		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
						            		Ext.each(data, function(d){
						            			d.deploy = true;
						            		});
						            		grid.data = data;
						            		if(res.dbfinds.length > 0){
						            			grid.dbfinds = res.dbfinds;
						            		}
						            		//取数据字典配置
						            		grid.getDataDictionaryData('CUSTOMTABLEDETAIL');
						            		grid.reconfigureGrid();
						        		}
						        	}
						        });
							},
							parseDictionary: function(dictionary) {
								var me = this, data = this.data;
								if(data.length==0){
									var v_detno=0;
									Ext.each(dictionary, function(d, index){
										if(d.column_name=='cd_id'||d.column_name=='cd_ctid'||d.column_name=='cd_detno'){
											o = new Object();
											o.dg_table = d.table_name;
											o.dg_field = d.column_name;
											o.dg_caption = d.comments;
											o.dg_captionfan = d.comments;
											o.dg_captionen = d.comments;
											o.dg_editable = false;
											o.dg_width = 80;
											o.dg_dbbutton = '0';
											o.dg_visible = true;
											o.deploy = true;
											o.dg_caller = me.whoami;
											if(contains(d.data_type, 'VARCHAR2', true)){
												o.dg_type = 'text';
												o.dg_maxlength=d.data_length;
											} else if(contains(d.data_type, 'TIMESTAMP', true)){
												o.dg_type = 'datetimecolumn';
											} else if(d.data_type == 'DATE'){
												o.dg_type = 'datecolumn';
											} else if(d.data_type == 'NUMBER'){
												o.dg_type = 'numbercolumn';
											} else if(d.data_type == 'FLOAT'){
												o.dg_type = 'floatcolumn';
											} else {
												o.dg_type = 'text';
												o.dg_maxlength=d.data_length||100;
											}
											o.dg_sequence = ++v_detno;
											if(d.column_name=='cd_id'){
												o.dg_logictype='keyField';
												o.dg_width = 0;
												o.dg_field = d.column_name.toUpperCase();
											}
											if(d.column_name=='cd_ctid'){
												o.dg_logictype='mainField';
												o.dg_field = d.column_name.toUpperCase();
												o.dg_width = 0;
											}
											if(d.column_name=='cd_detno'){
												o.dg_logictype='detno';
												o.dg_field = d.column_name.toUpperCase();
											}
											data.push(o);
										}
									});
								}
								//取Max(序号)
								var det = Ext.Array.max(Ext.Array.pluck(data, me.detno));
								//data里面包含的字段
								var sel = [];
								Ext.Array.each(data, function(d){
									sel.push(d.dg_field.toLowerCase());
								});
								var o = null;
								Ext.each(dictionary, function(d, index){
									//将DataDictionary的数据转化成FormDetail数据
									if(sel.indexOf(d.column_name) == -1){
										o = new Object();
										o.dg_table = d.table_name;
										o.dg_field = d.column_name;
										o.dg_caption = d.comments;
										o.dg_captionfan = d.comments;
										o.dg_captionen = d.comments;
										o.dg_editable = false;
										o.dg_width = 80;
										o.dg_dbbutton = '0';
										o.dg_visible = true;
										o.deploy = false;
										o.dg_caller = me.whoami;
										if(contains(d.data_type, 'VARCHAR2', true)){
											o.dg_type = 'text';
											o.dg_maxlength=d.data_length;
										} else if(contains(d.data_type, 'TIMESTAMP', true)){
											o.dg_type = 'datetimecolumn';
										} else if(d.data_type == 'DATE'){
											o.dg_type = 'datecolumn';
										} else if(d.data_type == 'NUMBER'){
											o.dg_type = 'numbercolumn';
										} else if(d.data_type == 'FLOAT'){
											o.dg_type = 'floatcolumn';
										} else {
											o.dg_type = 'text';
											o.dg_maxlength=d.data_length||100;
										}
										o.dg_sequence = ++det;
										data.push(o);
									}
								});
								me.dictionary = dictionary;
							}
						}]
					}]
				},{
					xtype: 'toolbar', 
					anchor: '100% 5%',
					items: ['->',{
						xtype: 'erpUUListenerButton'
					},'-',{
						iconCls: 'x-button-icon-preview',
						name: 'preview',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpPreviewButton
					},'-',{
						iconCls: 'tree-save',
						name: 'save',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpSaveButton
					},'-',{
						xtype: 'erpSyncButton'
					},'-',{
						iconCls: 'tree-delete',
						name: 'delete',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpDeleteButton
					},'-',{
						iconCls: 'tree-close',
						name: 'close',
						cls: 'x-btn-gray',
						text: $I18N.common.button.erpCloseButton
					},'->']
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});