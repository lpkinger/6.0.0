/**
 * 辅助核算按钮
 */	
Ext.define('erp.view.core.button.AssDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAssDetailButton',
		cls: 'x-btn-gray',
		id: 'assdetail',
    	text: $I18N.common.button.erpAssMainButton,
        GridUtil: Ext.create('erp.util.GridUtil'),
        BaseUtil: Ext.create('erp.util.BaseUtil'),
        cacheStoreForm: new Array(),
        width: 75,
		getGrid: function() {
			return this.ownerCt.ownerCt;
		},
		initComponent: function() {
			this.cacheStoreGrid = [];
			this.callParent(arguments);
		},
		listeners:{
			click: function(assBtn){
				var me = this;
				var grid = assBtn.getGrid();
				var record = grid.selModel.lastSelected;
				if(record){
					var id = record.get(grid.keyField) || (-grid.store.indexOf(record));
					var assGrid = Ext.create('Ext.grid.Panel', {
							anchor: '100% 100%',
							columns: [{
								text: 'ID',
								hidden: true,
								dataIndex: 'dass_id'
							},{
								text: 'DASS_CONDID',
								hidden: true,
								dataIndex: 'dass_condid'
							},{
								text: '辅助类型',
								dataIndex: 'dass_asstype',
								hidden: true,
								flex: 1
							},{
								text: '核算项',
								dataIndex: 'dass_assname',
								flex: 1
							},{
								text: '编号表达式',
								dataIndex: 'dass_codefield',
								flex: 1,
								editor: {
									xtype: 'dbfindtrigger',
									listeners:{
										focus: function(t){
						    				t.setHideTrigger(false);
						    				t.setReadOnly(false);//用disable()可以，但enable()无效
						    				var record = assGrid.selModel.lastSelected;
						    				var asstype = record.data['dass_asstype'];						    				
						    				if(asstype != 'Store' && asstype != 'Empl' && asstype != 'Dept' && asstype != 'Vend' && asstype != 'Cust'){
						    					t.dbBaseCondition ="ak_code='" + asstype + "'";
						    				} else 
						    					t.dbBaseCondition = null;
						    			}
									}
								}
							},{
								text: '名称表达式',
								dataIndex: 'dass_namefield',
								flex: 1,
								editor: {
									xtype: 'textfield'
								}
							}],
							store: new Ext.data.Store({
								fields: [{name: 'dass_id', type: 'number'}, {name: 'dass_condid', type: 'number'},
								         {name: 'dass_assname', type: 'string'},{name: 'dass_asstype', type: 'string'}, 
								         {name: 'dass_codefield', type: 'string'},
								         {name: 'dass_namefield', type: 'string'}]
							}),
							columnLines: true,
							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						        clicksToEdit: 1
						    })],
						    listeners:{
						    	afterrender:function(g){
						    		g.plugins[0].on('beforeedit',function(e){
						    			if(e.field=='dass_codefield'){
						    				var record = e.record, column = e.column;
						    				var set = me.getDbfindSet(record.get('dass_asstype'));
						    				g.dbfinds = set.dbfinds;
						    				column.dbfind = set.dbfind;
						    			}
						    		});
						    	}
						    }
						});
						var win = Ext.create('Ext.Window', {
							width: 500,
							height: 360,
							layout: 'anchor',
							title: '辅助核算',
							items: [assGrid],
							buttonAlign: 'center',
							modal: true,
							buttons: [{
								text: $I18N.common.button.erpConfirmButton,
								cls: 'x-btn-blue',
								handler: function(btn) {
									var win = btn.ownerCt.ownerCt,
										record = assBtn.getGrid().selModel.lastSelected,
										ass = win.down('gridpanel');
				    				var data = new Array();
				    				ass.store.each(function(item){
				    					data.push(item.data);
				    				});
				    				if(data.length > 0){
				    					assBtn.cacheStoreGrid[record.data[assBtn.getGrid().keyField] || (-assBtn.getGrid().store.indexOf(record))] = data;
				    				}
				    				win.close();
								}
							},{
								text: $I18N.common.button.erpOffButton,
								cls: 'x-btn-blue',
								handler: function(btn) {
									btn.ownerCt.ownerCt.close();
								}
							}]
						}).show();
						assBtn.getAssGrid(win.down('gridpanel'), id, record.get('ca_assname'),record.get('ca_asstype'));
				}
			}
		},
		getDbfindSet: function(type) {
			var sets = {
				'Vend': {
					dbfind: 'Vendor|ve_code',
					dbfinds: [{dbGridField:'ve_code',field:'dass_codefield'},{dbGridField:'ve_name',field:'dass_namefield'}]
				},
				'Dept': {
					dbfind: 'Department|dp_code',
					dbfinds: [{dbGridField:'dp_code',field:'dass_codefield'},{dbGridField:'dp_name',field:'dass_namefield'}]
				},
				'Empl': {
					dbfind: 'Employee!ALL|em_code',
					dbfinds: [{dbGridField:'em_code',field:'dass_codefield'},{dbGridField:'em_name',field:'dass_namefield'}]
				},
				'Store': {
					dbfind: 'WareHouse|wh_code',
					dbfinds: [{dbGridField:'wh_code',field:'dass_codefield'},{dbGridField:'wh_description',field:'dass_namefield'}]
				},
				'Cust': {
					dbfind: 'Customer|cu_code',
					dbfinds: [{dbGridField:'cu_code',field:'dass_codefield'},{dbGridField:'cu_name',field:'dass_namefield'}]
				},
				'Otp': {
					dbfind: 'Project!Ass|prj_code',
					dbfinds: [{dbGridField:'prj_code',field:'dass_codefield'},{dbGridField:'prj_name',field:'dass_namefield'}]
				}
			};
			return sets[type] || {
				dbfind: 'AssKindDetail|akd_asscode',
				dbfinds: [{dbGridField:'akd_asscode',field:'dass_codefield'},{dbGridField:'akd_assname',field:'dass_namefield'}]
			};
		},
	    getAssGrid: function(grid, id,assname,asstype) {
	    	var me = this;
			if(!me.cacheStoreGrid[id]){
				if(id == null || id <= 0){
					var data = new Array(),r = assname.split('#'),t = asstype.split('#');
					for(var i=0;i<r.length;i++){
						var o = new Object();
						o.dass_condid = id;
						o.dass_assname = r[i];
						o.dass_asstype = t[i];
						data.push(o);
					}
					grid.store.loadData(data);
				} else {
					var condition = "dass_condid=" + id;
					Ext.Ajax.request({
			        	url : basePath + 'common/getFieldsDatas.action',
			        	params: {
			        		caller: me.ownerCt.ownerCt.detailAssCaller,
			        		fields: 'dass_id,dass_condid,dass_assname,dass_codefield,dass_namefield,dass_asstype',
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
			        		var dd = new Array(),r = assname.split('#'),t = asstype.split('#');
							for(var i=0;i<r.length;i++){
								var o = new Object();
								Ext.Array.each(data, function(d){
									if(d.DASS_ASSNAME == r[i]) {
										o.dass_id = d.DASS_ID;
										o.dass_condid = d.DASS_CONDID;
										o.dass_assname = d.DASS_ASSNAME;
										o.dass_asstype = t[i];
										o.dass_codefield = d.DASS_CODEFIELD;
										o.dass_namefield = d.DASS_NAMEFIELD;
										dd.push(o);
									}
								});
								if(o.dass_id == null) {
									o.dass_condid = id;
									o.dass_assname = r[i];
									o.dass_asstype = t[i];
									dd.push(o);
								}
							}
							if(dd.length == 0) {
								dd = [{}, {}, {}, {}, {},{}];
							}
							grid.store.loadData(dd);
			        	}
			        });
				}
			} else {
				grid.store.loadData(me.cacheStoreGrid[id]);
			}
	    }
	});