Ext.QuickTips.init();
Ext.define('erp.controller.fa.VoucherStyle2', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views: ['fa.VoucherStyle', 'core.form.Panel', 'core.grid.Panel2', 'core.grid.YnColumn', 'core.trigger.TextAreaTrigger',
            'core.trigger.DbfindTrigger','core.button.Sync','core.button.CreateSQL',
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
    		'erpCreateSQLButton': {
    			click: function(btn){
    				warnMsg("确定要生成SQL语句吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    						var type = items[0].data['vd_class']
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/vc/createSql.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('vs_id').value,
    	    			   			type: type
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				//辅助核算
    				btn.ownerCt.add({
    					text: '辅助核算',
    					width: 85,
    					disabled: true,
    			    	cls: 'x-btn-blue',
    			    	id: 'assdetail'
    				});
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
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var id = record.get('vd_id') || (-grid.store.indexOf(record));
    					var win = Ext.getCmp('ass-' + id);
    					if(win) {
    						win.show();
    					} else {
    						var grid = Ext.create('Ext.grid.Panel', {
    							anchor: '100% 100%',
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
    							width: 500,
    							height: 360,
    							layout: 'anchor',
    							title: '辅助核算公式',
    							items: [grid],
    							buttonAlign: 'center',
    							modal: true,
    							buttons: [{
    								text: $I18N.common.button.erpConfirmButton,
    								cls: 'x-btn-blue',
    								handler: function(btn) {
    									var win = btn.ownerCt.ownerCt,
    										grid = Ext.getCmp('grid'),
    										record = grid.selModel.lastSelected,
    										ass = win.down('gridpanel');
    				    				var data = new Array();
    				    				ass.store.each(function(item){
    				    					data.push(item.data);
    				    				});
    				    				if(data.length > 0){
    				    					me.cacheStore[record.data[grid.keyField] || (-record.index)] = data;
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
    					me.getAss(win.down('gridpanel'), id, record.get('ca_assname'));
    				}
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
    		},
    		'dbfindtrigger[name=vd_casdesc]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);
 				   if(Ext.getCmp('vs_pritable')){
 					   var code = Ext.getCmp('vs_pritable').value;
 					   if(code != null && code != ''){
 						  if(t.dbBaseCondition==null||t.dbBaseCondition==''){
							   t.dbBaseCondition= "cas_code='"+code+"'";
						   }else{
							   t.dbBaseCondition=t.dbBaseCondition+" and cas_code='"+code+"'";
						   }
 					   }
 				   }
 			   }
    		},
    		'dbfindtrigger[name=vd_amsdesc]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('vs_pritable')){
  					   var code = Ext.getCmp('vs_pritable').value;
  					   if(code != null && code != ''){
  						  if(t.dbBaseCondition==null||t.dbBaseCondition==''){
 							   t.dbBaseCondition= "ams_code='"+code+"'";
 						   }else{
 							   t.dbBaseCondition=t.dbBaseCondition+" and ams_code='"+code+"'";
 						   }
  					   }
  				   }
  			   }
     		},
     		'dbfindtrigger[name=vd_cusdesc]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('vs_pritable')){
  					   var code = Ext.getCmp('vs_pritable').value;
  					   if(code != null && code != ''){
  						  if(t.dbBaseCondition==null||t.dbBaseCondition==''){
 							   t.dbBaseCondition= "cus_code='"+code+"'";
 						   }else{
 							   t.dbBaseCondition=t.dbBaseCondition+" and cus_code='"+code+"'";
 						   }
  					   }
  				   }
  			   }
     		},
     		'dbfindtrigger[name=vd_rasdesc]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('vs_pritable')){
  					   var code = Ext.getCmp('vs_pritable').value;
  					   if(code != null && code != ''){
  						  if(t.dbBaseCondition==null||t.dbBaseCondition==''){
 							   t.dbBaseCondition= "ras_code='"+code+"'";
 						   }else{
 							   t.dbBaseCondition=t.dbBaseCondition+" and ras_code='"+code+"'";
 						   }
  					   }
  				   }
  			   }
     		},
     		'dbfindtrigger[name=vd_resdesc]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('vs_pritable')){
  					   var code = Ext.getCmp('vs_pritable').value;
  					   if(code != null && code != ''){
  						  if(t.dbBaseCondition==null||t.dbBaseCondition==''){
 							   t.dbBaseCondition= "res_code='"+code+"'";
 						   }else{
 							   t.dbBaseCondition=t.dbBaseCondition+" and res_code='"+code+"'";
 						   }
  					   }
  				   }
  			   }
     		},
     		'field[name=vd_explanation]': {
    			focus: function(f){
    				var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					me.explainView(record);
    			}
    		},
    	});
    },
    explainView:function(gridrecord){
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
			height = Ext.isIE ? screen.height*0.75 : '80%';
		var code = Ext.getCmp('vs_pritable').value, type = gridrecord.data['vd_class'];
		if(!this.explainWin) {
			this.explainWin = Ext.create('Ext.Window', {
				title : '摘要定义',
				closeAction: 'hide',
				width: width,
				height: height,
				autoShow: true,
				layout: 'anchor',
				items: [{
					xtype : 'grid',
					frame : true,
					anchor: '100% 60%',
					id : 'grid1',
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
						remoteFilter: true
					})],
					tbar : [ '->', {
						text : $I18N.common.button.erpConfirmButton,
					    iconCls: 'x-button-icon-close',
					    cls: 'x-btn-gray',
					    handler : function(btn){
					    	gridrecord.set('vd_explanation',btn.up('window').down('htmleditor').getFormatValue());
					    	btn.up('window').close();
					    }
					}, {
						text : $I18N.common.button.erpCancelButton,
					    iconCls: 'x-button-icon-close',
					    cls: 'x-btn-gray',
					    handler : function(btn){
					    	btn.up('window').close();
					    }
					}],
					columns : [{
						text : '可选择摘要单元',
						cls : 'x-grid-header-1',
						dataIndex: 'DIS_DESCRIPTION',
						flex: 2,
						filter: {
		    				xtype : 'textfield'
		    			}
					},{
						text : '编号',
						cls : 'x-grid-header-1',
						dataIndex: 'DIS_CODE',
						flex: 1,
						filter: {
		    				xtype : 'textfield'
		    			}
					},{
						text : '单据类型',
						cls : 'x-grid-header-1',
						dataIndex: 'DIS_CLASS',
						flex: 1,
						filter: {
		    				xtype : 'textfield'
		    			}
					}],
					store : new Ext.data.Store({
						fields : [ 'DIS_DESCRIPTION', 'DIS_CODE', 'DIS_CLASS',
								'DIS_ID','DIS_SQLPARAM' ],
						proxy : {
							type : 'ajax',
							url : basePath + 'fa/vc/getDigestSource.action',
							reader : {
								type : 'json',
								root : 'data'
							}
						},
						autoLoad : {
							params: {
								code: code,
								type: type
							}
						},
						listeners: {
							load: function(store, datas) {
								var sqlparams = gridrecord.get('vd_explanation');
								if(sqlparams && datas) {
									var editor = Ext.getCmp('parameditor');
									editor.parseSql(sqlparams, datas);
								}
							}
						}
					}),
					listeners:{
						itemmousedown:function(selmodel, record){
							var grid = selmodel.ownerCt, editor = grid.ownerCt.down('#parameditor');
							var desc = record.data['DIS_DESCRIPTION'], param = record.get('DIS_SQLPARAM');
							editor.insertCell(desc, param);
		    	    	}
		    	    }
				},{
					xtype : 'htmleditor',
					anchor : '100% 40%',
					id: 'parameditor',
					enableColors: false,
			        enableAlignments: false,
			        enableFont: false,
			        enableFontSize: false,
			        enableFormat: false,
			        enableLinks: false,
			        enableLists: false,
			        enableSourceEdit: false,
					insertCell: function(desc, param) {
						var me = this, src = this.textToDataURL(desc, {fontSize: 14, color: '#ff0000'});
	                    me.win.focus();
	                    me.execCmd('InsertHTML', '<img src="' + src +'" title="' + desc + '" style="margin-bottom: -2px;" data-code="' + param + '"/>');
	                    me.deferFocus();
					},
					textToDataURL: function(text, opts) {
						var canvas = this.getCanvas(), context = canvas.getContext('2d');
						text = '[' + text + ']';
						var offset = this.getCharOffset(text, opts.fontSize);
						canvas.width = offset.width;
					    canvas.height = offset.height;
				        context.clearRect(0, 0, canvas.width, canvas.height);
				        context.fillStyle = opts.color;
				        context.font = 'normal ' + opts.fontSize + 'px \'microsoft yahei\', sans-serif';
				        context.textBaseline = 'top';
				        canvas.style.display = 'none';
				        context.fillText(text, 0, 0, canvas.width);
				        return canvas.toDataURL("image/png");
					},
					/**
					 * html5画布
					 **/
					getCanvas: function() {
						if(Ext.supports.Canvas) {
							var me = this;
							if(!me.canvas) {
								Ext.DomHelper.append(Ext.getBody(), {
									tag: 'canvas',
									id: me.getId() + '-canvas',
									style: 'display:block;'
								});
								me.canvas = Ext.get(me.getId() + '-canvas').dom;
							}
							return me.canvas;
						} else {
							Ext.Msg.alert('您的浏览器暂不支持！');
						}
					},
					/**
					 * 预先计算即将插入的文字所占宽度和高度
					 **/
					getCharOffset: function(str, fontSize) {
						var me = this;
						if(!me.charWidthSpan) {
							Ext.DomHelper.append(Ext.getBody(), {
								tag: 'span',
								id: me.getId() + '-char-width-span',
								style: 'visibility:hidden;white-space:nowrap;font-size:' + fontSize + 'px'
							});
							me.charWidthSpan = Ext.get(me.getId() + '-char-width-span').dom;
						}
						me.charWidthSpan.innerText = str;
					    return {width: me.charWidthSpan.offsetWidth, height: me.charWidthSpan.offsetHeight};
					},
					/**
					 * 转化成sql代码
					 */
					getFormatValue: function() {
						var val = this.getValue();
						if(val) {
							var imgs = this.getEditorBody().getElementsByTagName('img');
							Ext.Array.each(imgs, function(img){
								val = val.replace(img.outerHTML, '\'||' + img.getAttribute('data-code') + '||\'');
				            });
							val = ('\'' + val + '\'').replace(/(''\|\|)|(\|\|'')/gi,'');
						}
						return val;

					},
					parseSql: function(sqlValue, datas) {
						var me = this, sqls = sqlValue.split('||');
						Ext.defer(function(){
							me.setValue(null);
							if (!me.activated) {
								me.onFirstFocus();
							}
							me.win.focus();
							Ext.Array.each(sqls, function(sql){
								if(/'.+'/.test(sql)) {
									me.insertAtCursor(sql.substring(1, sql.length - 1));
								} else {
									Ext.Array.each(datas, function(data){
										if(data.get('DIS_SQLPARAM') == sql) {console.log(sql);
											me.insertCell(data.get('DIS_DESCRIPTION'), data.get('DIS_SQLPARAM'));
											return;
										}
									});
								}
							});
						}, !me.win ? 500 : 1, me);
					}
				}]
			});
		} else {
			this.explainWin.down('#parameditor').setValue(null);
			this.explainWin.down('#grid1').store.load({
				params: {
					code: code,
					type: type
				}
			});
		}
		this.explainWin.show();
	},
    _beforeSave: function() {
    	var grid = Ext.getCmp('grid'),
    		code = Ext.getCmp('vs_code').value;
    	
//    	Ext.each(grid.store.data.items,function(item,index){
//    		
//    		if(item.data['vd_class']==''){
//    			item.data['vd_code']=code;
//    		}
//    		
//    	});
    	
    	grid.store.each(function(d){
    		if(!Ext.isEmpty(d.get('vd_class'))) {
    			d.set('vd_code', code);
    		}
    	});
    },
    cacheStore: new Array(),
    getAss: function(grid, id, assname) {
    	var me = this;
		if(!me.cacheStore[id]){
			if(id == null || id <= 0){
				var data = new Array(),r = assname.join('#');
				for(var i=0;i<r.length;i++){
					var o = new Object();
					o.vsa_vdid = id;
					o.vsa_assname = r[i];
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
		        		var dd = new Array(),r = assname.split('#');
						Ext.Array.each(data, function(d){
							var o = new Object();
							o.vsa_id = d.VSA_ID;
							o.vsa_vdid = d.VSA_VDID;
							o.vsa_assname = d.VSA_ASSNAME;
							o.vsa_codefield = d.VSA_CODEFIELD;
							o.vsa_namefield = d.VSA_NAMEFIELD;
							dd.push(o);
						});
						for(var i = 0; i < r.length; i++){
		        			if(!Ext.isEmpty(r[i])) {
		        				var bool = false;
								Ext.Array.each(data, function(d){
									if(d.VSA_ASSNAME == r[i]) {
										bool = true;
									}
								});
								if(!bool) {
									var o = new Object();
									o.vsa_vdid = id;
									o.vsa_assname = r[i];
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
		Ext.each(Ext.Object.getKeys(me.cacheStore), function(key){
			Ext.each(me.cacheStore[key], function(d){
				d['vsa_vdid'] = key;
				param2.push(d);
			});
		});
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0)
				&& param2.length == 0){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				me.FormUtil.update(form.getValues(), param1, param2);
			}else{
				me.FormUtil.checkForm();
			}
		}
	}
});