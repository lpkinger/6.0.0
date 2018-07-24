Ext.QuickTips.init();
Ext.define('erp.controller.fa.VoucherStyle', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views: ['fa.VoucherStyle', 'core.form.Panel', 'core.grid.Panel2', 'core.grid.YnColumn', 'core.trigger.TextAreaTrigger',
            'core.trigger.DbfindTrigger','core.button.Sync', 'core.button.GroupSet',
            'core.button.Close', 'core.button.Save', 'core.button.Update'],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			itemclick: function(selModel, record) {
    				me.GridUtil.onGridItemClick(selModel, record);
    				var btn = Ext.getCmp('assdetail');
    				var ass = record.data['ca_asstype'],
    					check = record.data['vd_checkitem'];
    				if(!Ext.isEmpty(ass) || check == -1){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				me._beforeSave();
    				me.beforeSave();
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me._beforeSave();
    				me.beforeUpdate();
    			}
    		},
    		'button[id=assdetail]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var id = record.get('vd_id') || (-grid.store.indexOf(record));
    					var win = Ext.getCmp('ass-' + id);
    					if(win) {
    						win.show();
    					} else {
    						var form = Ext.create('Ext.form.Panel', {
    							height: 160,
    							layout: 'column',
    							defaults: {
    								xtype: 'textfield',
    								columnWidth: .5,
    								margin: 3,
    								allowBlank: false
    							},
    							bodyStyle: 'background:transparent',
    							items: [{
    								fieldLabel: '表名',
    								name: 'vd_asstable'
    							},{
    								fieldLabel: '关联关系',
    								name: 'vd_assrel',
    								columnWidth: 1
    							},{
    								fieldLabel: '类型字段',
    								name: 'vd_asstypef'
    							},{
    								fieldLabel: '编号字段',
    								name: 'vd_asscodef'
    							},{
    								fieldLabel: '名称字段',
    								name: 'vd_assnamef'
    							}]
    						});
    						var grid = Ext.create('Ext.grid.Panel', {
    							height: 200,
    							columns: [{
    								text: 'ID',
    								hidden: true,
    								dataIndex: 'vsa_id'
    							},{
    								text: 'VD_ID',
    								hidden: true,
    								dataIndex: 'vsa_vdid'
    							},{
    								text: '核算项',
    								dataIndex: 'vsa_assname',
    								flex: 1,
    								editor: {
    									xtype: 'dbfindtrigger'
    								},
    								dbfind: 'AssKind|ak_name'
    							},{
    								text: '编号表达式',
    								dataIndex: 'vsa_codefield',
    								flex: 1,
    								editor: {
    									xtype: 'textfield'
    								}
    							},{
    								text: '名称表达式',
    								dataIndex: 'vsa_namefield',
    								flex: 1,
    								editor: {
    									xtype: 'textfield'
    								}
    							}],
    							store: new Ext.data.Store({
    								fields: [{name: 'vsa_id', type: 'number'}, {name: 'vsa_vdid', type: 'number'},
    								         {name: 'vsa_assname', type: 'string'}, {name: 'vsa_codefield', type: 'string'},
    								         {name: 'vsa_namefield', type: 'string'}]
    							}),
    							columnLines: true,
    							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    						        clicksToEdit: 1
    						    })],
    						    dbfinds: [{
    						    	field: 'vsa_assname',
    						    	dbGridField: 'ak_name'
    						    }], 
    						    listeners: {
    						    	itemclick: function(selModel, record) {
    						    		var grid = selModel.ownerCt,
    						    			store = grid.store,
    						    			idx = store.indexOf(record),
    						    			len = store.getCount();
    						    		if(idx == len - 1) {
    						    			store.add({}, {}, {}, {}, {});
    						    		}
    						    	}
    						    }
    						});
    						win = Ext.create('Ext.Window', {
    							width: 600,
    							height: '80%',
    							title: '辅助核算公式',
    							bodyStyle: 'padding: 10px;',
    							autoScroll: true,
    							items: [{
    								xtype: 'fieldset',
    								collapsible: true,
    								title: '常规核算',
    								padding: 10,
    								autoScroll: true,
    								items: [grid]
    							},{
    								xtype: 'fieldset',
    								collapsible: true,
    								title: '第三方核算表',
    								padding: 10,
    								autoScroll: true,
    								items: [form]
    							}],
    							buttonAlign: 'center',
    							modal: true,
    							buttons: [{
    								text: $I18N.common.button.erpConfirmButton,
    								cls: 'x-btn-blue',
    								handler: function(btn) {
    									var win = btn.ownerCt.ownerCt,
    										grid = Ext.getCmp('grid'),
    										record = grid.selModel.lastSelected,
    										assgrid = win.down('gridpanel'), assform = win.down('form');
    									if (assform.getForm().isValid()) {
    										record.set('vd_asstable', assform.down('field[name=vd_asstable]').getValue());
    										record.set('vd_assrel', assform.down('field[name=vd_assrel]').getValue());
    										record.set('vd_asstypef', assform.down('field[name=vd_asstypef]').getValue());
    										record.set('vd_asscodef', assform.down('field[name=vd_asscodef]').getValue());
    										record.set('vd_assnamef', assform.down('field[name=vd_assnamef]').getValue());
    										me.cacheStore[record.data[grid.keyField] || (-record.index)] = [];
    									} else {
    										record.set('vd_asstable', null);
    										record.set('vd_assrel', null);
    										record.set('vd_asstypef', null);
    										record.set('vd_asscodef', null);
    										record.set('vd_assnamef', null);
    										var data = new Array();
        				    				assgrid.store.each(function(item){
        				    					data.push(item.data);
        				    				});
        				    				if(data.length > 0){
        				    					me.cacheStore[record.data[grid.keyField] || (-record.index)] = data;
        				    				}
    									}
    				    				win.hide();
    								}
    							},{
    								text: $I18N.common.button.erpOffButton,
    								cls: 'x-btn-blue',
    								handler: function(btn) {
    									btn.ownerCt.ownerCt.hide();
    								}
    							}]
    						}).show();
    					}
    					me.getAss(win.down('form'), win.down('gridpanel'), id, record);
    				}
    			}
    		},
    		'erpGroupSetButton': {
    			click: function(btn){
    				var id = Ext.getCmp('vs_id').value;
    				var win = Ext.getCmp('group-' + id);
					if(win) {
						win.show();
					} else {
						var grid = Ext.create('Ext.grid.Panel', {
							height: '100%',
							columns: [{
								text: 'ID',
								hidden: true,
								dataIndex: 'vsg_id'
							},{
								text: 'VS_ID',
								hidden: true,
								dataIndex: 'vsg_vsid'
							},{
								text: '合并描述',
								dataIndex: 'vsg_groupname',
								flex: 1,
								editor: {
									xtype: 'textfield'
								}
							},{
								text: '合并字段',
								dataIndex: 'vsg_groupfield',
								flex: 1,
								editor: {
									xtype: 'textfield'
								}
							}],
							store: new Ext.data.Store({
								fields: [{name: 'vsg_id', type: 'number'}, {name: 'vsg_vsid', type: 'number'},
								         {name: 'vsg_groupname', type: 'string'}, {name: 'vsg_groupfield', type: 'string'}]
							}),
							columnLines: true,
							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						        clicksToEdit: 1
						    })],
						    listeners: {
						    	itemclick: function(selModel, record) {
						    		var grid = selModel.ownerCt,
						    			store = grid.store,
						    			idx = store.indexOf(record),
						    			len = store.getCount();
						    		if(idx == len - 1) {
						    			store.add({}, {}, {}, {}, {});
						    		}
						    	}
						    }
						});
						win = Ext.create('Ext.Window', {
							width: 500,
							height: 280,
							title: '凭证合并设置',
							autoScroll: true,
							items: [grid],
							buttonAlign: 'center',
							modal: true,
							buttons: [{
								text: $I18N.common.button.erpConfirmButton,
								cls: 'x-btn-gray',
								handler: function(btn) {
									var win = btn.ownerCt.ownerCt,
										groupgrid = win.down('gridpanel');
									var data = new Array();
									groupgrid.store.each(function(item){
				    					data.push(item.data);
				    				});
				    				if(data.length > 0){
				    					me.groupStore[id] = data;
				    				}
				    				win.hide();
								}
							},{
								text: $I18N.common.button.erpOffButton,
								cls: 'x-btn-gray',
								handler: function(btn) {
									btn.ownerCt.ownerCt.hide();
								}
							}]
						}).show();
					}
					me.getGroup(win.down('gridpanel'), id);
    			}
    		},
    		'field[name=vd_catecode]': {
    			aftertrigger: function(f, d) {
    				var record = f.record,
    					ass = d.get('ca_asstype');
    				if(!Ext.isEmpty(ass)) {
    					record.set('vd_checkitem', -1);
    				} else {
    					record.set('vd_checkitem', 0);
    				}
    			}
    		}
    	});
    },
    _beforeSave: function() {
    	var grid = Ext.getCmp('grid'),
    		code = Ext.getCmp('vs_code').value;
    	
    	grid.store.each(function(d){
    		if(!Ext.isEmpty(d.get('vd_class'))) {
    			d.set('vd_code', code);
    		}
    	});
    },
    cacheStore: new Array(),
    getAss: function(form, grid, id, record) {
    	var me = this, ass = record.get('ca_assname').split('#');
    	if(!Ext.isEmpty(record.get('vd_asstable'))) {
    		form.getForm().setValues(record.data);
    	}
    	if(!me.cacheStore[id]){
			if(id == null || id <= 0){
				var data = new Array();
				for(var i = 0; i < ass.length; i++){
					var o = new Object();
					o.vsa_vdid = id;
					o.vsa_assname = ass[i];
					data.push(o);
				}
				grid.store.loadData(data);
			} else {
				var condition = "vsa_vdid=" + id;
				Ext.Ajax.request({
		        	url : basePath + 'common/getFieldsDatas.action',
		        	params: {
		        		caller: "VoucherStyleAss",
		        		fields: 'vsa_id,vsa_vdid,vsa_assname,vsa_codefield,vsa_namefield',
		        		condition: condition
		        	},
		        	method : 'post',
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);
		        		if(res.exception || res.exceptionInfo){
		        			showError(res.exceptionInfo);
		        			return;
		        		}
		        		var data = Ext.decode(res.data);
		        		var dd = new Array();
						Ext.Array.each(data, function(d){
							var o = new Object();
							o.vsa_id = d.VSA_ID;
							o.vsa_vdid = d.VSA_VDID;
							o.vsa_assname = d.VSA_ASSNAME;
							o.vsa_codefield = d.VSA_CODEFIELD;
							o.vsa_namefield = d.VSA_NAMEFIELD;
							dd.push(o);
						});
						for(var i = 0; i < ass.length; i++){
		        			if(!Ext.isEmpty(ass[i])) {
		        				var bool = false;
								Ext.Array.each(data, function(d){
									if(d.VSA_ASSNAME == ass[i]) {
										bool = true;
									}
								});
								if(!bool) {
									var o = new Object();
									o.vsa_vdid = id;
									o.vsa_assname = ass[i];
									dd.push(o);
								}
		        			}
						}
						if(dd.length == 0) {
							dd = [{}, {}, {}, {}, {}];
						}
						grid.store.loadData(dd);
		        	}
		        });
			}
		} else {
			grid.store.loadData(me.cacheStore[id]);
		}
    },
    groupStore: new Array(),
    getGroup: function(grid, id) {
    	var me = this;
    	if(!me.groupStore[id]){
			if(id == null || id <= 0){
				grid.store.each(function(d){
		    		if(!Ext.isEmpty(d.get('vsg_groupname'))) {
		    			d.set('vsg_vsid', id);
		    		}
		    	});
			} else {
				var condition = "vsg_vsid=" + id;
				Ext.Ajax.request({
		        	url : basePath + 'common/getFieldsDatas.action',
		        	params: {
		        		caller: "VOUCHERSTYLEGROUP",
		        		fields: 'vsg_id,vsg_vsid,vsg_groupname,vsg_groupfield',
		        		condition: condition
		        	},
		        	method : 'post',
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);
		        		if(res.exception || res.exceptionInfo){
		        			showError(res.exceptionInfo);
		        			return;
		        		}
		        		var data = Ext.decode(res.data);
		        		var dd = new Array();
						Ext.Array.each(data, function(d){
							var o = new Object();
							o.vsg_id = d.VSG_ID;
							o.vsg_vsid = d.VSG_VSID;
							o.vsg_groupname = d.VSG_GROUPNAME;
							o.vsg_groupfield = d.VSG_GROUPFIELD;
							dd.push(o);
						});
						if(dd.length == 0) {
							dd = [{}, {}, {}, {}, {}];
						}
						grid.store.loadData(dd);
		        	}
		        });
			}
		} else {
			grid.store.loadData(me.groupStore[id]);
		}
    },
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var param2 = new Array();
		Ext.each(Ext.Object.getKeys(me.cacheStore), function(key){
			Ext.each(me.cacheStore[key], function(d){
				d['vsa_vdid'] = key;
				param2.push(d);
			});
		});
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		me.onSave(form, param1, param2);
	},
	onSave: function(form, param1, param2) {
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
//		param3 = param3 == null ? [] : param3.toString().replace(/\\/g,"%");
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			me.FormUtil.save(form.getValues(), param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var detail = Ext.getCmp('grid');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		var param3 = new Array();
		Ext.each(Ext.Object.getKeys(me.cacheStore), function(key){
			Ext.each(me.cacheStore[key], function(d){
				d['vsa_vdid'] = key;
				param2.push(d);
			});
		});
		Ext.each(Ext.Object.getKeys(me.groupStore), function(key){
			Ext.each(me.groupStore[key], function(d){
				if(!Ext.isEmpty(d['vsg_groupname'])){
					d['vsg_vsid'] = key;
					param3.push(d);
				}
			});
		});
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0)
				&& param2.length == 0){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
			param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				me.FormUtil.update(form.getValues(), param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}
		}
	}
});