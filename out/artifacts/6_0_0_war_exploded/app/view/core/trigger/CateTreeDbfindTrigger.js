/**
 *  multi dbfind trigger
 * 
 */
Ext.define('erp.view.core.trigger.CateTreeDbfindTrigger', {
    				   extend: 'Ext.form.field.Trigger',
    				   alias: 'widget.cateTreeDbfindTrigger',
    				   triggerCls: 'x-form-search-trigger',
    				   initComponent: function() {
    					   this.addEvents({
    							aftertrigger: true,
    							beforetrigger: true
    					   });
    					   this.callParent(arguments);  
    				   },
    				   mode: 'SINGLE',
    				   listeners: {
    					   focus: function(f){
    						   if(!f.readOnly) {
    							   var trigger = this;
        						   trigger.lastTriggerId = trigger.id;
        						   if(!trigger.ownerCt){
        							   if(!trigger.owner){
        								   var grid = Ext.ComponentQuery.query('gridpanel');
    	   		    				   	   Ext.Array.each(grid, function(g, index){
    	   		    				   	    	Ext.Array.each(g.columns, function(column){
    	   		            				   		if(column.dataIndex == trigger.name) {
    	   		            				   			dbfind = column.dbfind;
    	   		            				   			trigger.owner = g;
    	   		            				   		}
    	   		            				   	});
    	   		    				   	   });
        							   }
        							   if(trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
    									   trigger.record = trigger.owner.editingPlugin.activeRecord;
    								   } else {
    									   trigger.record = trigger.owner.selModel.lastSelected;
    								   }
        							   var index = trigger.record.index;
    								   if(index != null){
    									   trigger.lastTriggerId = trigger.id + '---' + index;
    								   } else {
    									   trigger.lastTriggerId = null;
    								   }
        						   }
    						   }
    					   },
    					   blur: function(f){
    						   if(this.lastTriggerId && !f.readOnly){
    							   var which = 'form';
    							   var cal = caller;
    							   var key = this.name;
    							   var con = key + " like '%" + this.value + "%'";
    							   var currrecord = null;
    							   if(contains(this.lastTriggerId, '---', true) && !this.ownerCt){
    								   which = 'grid';
    								   var grid = Ext.ComponentQuery.query('gridpanel');
    		    				   	   Ext.Array.each(grid, function(g, index){
    		    				   	    	Ext.Array.each(g.columns,function(column){
    		            				   		if(column.dataIndex == key) {
    		            				   			dbfind = column.dbfind;
    		            				   		}
    		            				   	});
    		    				   	   });
    								   cal = dbfind.split('|')[0];
    								   con = dbfind.split('|')[1] + " like '%" + this.value + "%'";
    								   currrecord = this.owner.selModel.lastSelected;
    								   if(this.value != null && this.value != ''){
    									   var record = this.owner.store.getAt(this.lastTriggerId.split('---')[1]);
        								   this.owner.selModel.select(record);
    								   }
    							   }
    							   if(this.value != null && this.value != '' && this.lastTriggerId && 
    									   !this.readOnly && this.autoDbfind){
    								   this.autoDbfind(which, cal, key, con);//光标移开后自动dbfind
    								   if(currrecord){
    									   this.owner.selModel.select(currrecord);
    								   }
    							   }
    						   }
    					 }
    				   },
     				    onTriggerClick: function() {
    				   	var trigger = this;//放大镜所在
    				   	var key = this.name;//name属性
    				   	var dbfind = '';//需要dbfind的表和字段
    				   	var keyValue = this.value;//当前值
    				   	var record = null;
    				   	if(!trigger.ownerCt){
    				   		if(trigger.owner.selModel){
    				   			record = trigger.owner.selModel.selected.items[0];
    				   		}
    				   	}
    				   	var dbwin = Ext.getCmp('cate-dbwin');
    				   	if(!dbwin) {
    				   		dbwin = new Ext.window.Window({
    	   			    		id : 'cate-dbwin',
    		   				    title: '查找',
    		   				    height: "100%",
    		   				    width: "80%",
    		   				    maximizable : true,
    		   					buttonAlign : 'center',
    		   					layout : 'anchor',
    		   					modal:true,
    		   				    items: [],
    		   				    buttons : [{
    		   				    	text : '确  认',
    		   				    	iconCls: 'x-button-icon-save',
    		   				    	cls: 'x-btn-gray',
    		   				    	handler : function(){
    		   				    		var contentwindow = Ext.getCmp('cate-dbwin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
    		   				    		var tree = contentwindow.Ext.getCmp('tree-panel');
    		   				    		var data = tree.getChecked();
    		   				    		if(trigger.mode == 'MULTI') {
    		   				    			trigger.setMultiData(data);
    		   				    			Ext.getCmp('cate-dbwin').hide();
    		   				    			return;
    		   				    		}
    		   				    		var dbfinds;
    		   				    		var catecode = data[0].data.qtip;
    		   				    		var cateid = data[0].data.id;
    		   				    		var catecurrency = data[0].raw.currency;
    		   				    		var catename = data[0].raw.caname;
    		   				    		var catetypename = data[0].raw.typename;
    		   				    		var cateclass = data[0].raw.caclass;
    		   				    		var cateasstype = data[0].raw.caasstype;
    		   				    		var cateassname = data[0].raw.caassname;
    		   				    		var calevel = data[0].raw.calevel;
    		   				    		if(!trigger.ownerCt){
    		   				    			dbfinds = trigger.owner.dbfinds;
    		   				    			if(dbfinds!=null){
    		   				    				var keys = Ext.Object.getKeys(data[0].raw.data);
    			   				    			Ext.each(dbfinds,function(dbfind,index){
    			   				    				if(Ext.isEmpty(dbfind.trigger) || dbfind.trigger == trigger.name) {
    			   				    					var ss = dbfind.dbGridField.split(';');
    				   				    				for(var i in ss) {
    				   				    					if(Ext.Array.contains(keys, ss[i])) {
    					   				    					record.set(dbfind.field, data[0].raw.data[ss[i]]);
    					   				    				}
    				   				    				}
    			   				    				}
    			   				    			});
    		   				    			}
    		   				    		} else {
    		   				    			//form 中的
    		   				    			dbfinds = tree.dbfinds;
    		   				    			if(dbfinds && dbfinds.length > 0) {
    		   				    				Ext.each(dbfinds, function(dbfind, index){
    			   				    				if(dbfind.ca_id){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_id,value:cateid}]);
    			   				    				}
    			   				    				if(dbfind.ca_code){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_code,value:catecode}]);
    			   				    				}
    			   				    				if(dbfind.ca_name){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_name,value:catename}]);
    			   				    				}
    			   				    				if(dbfind.ca_currency){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_currency,value:catecurrency}]);
    			   				    				}
    			   				    				if(dbfind.ca_typename){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_typename,value:catetypename}]);
    			   				    				}
    			   				    				if(dbfind.ca_class){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_class,value:cateclass}]);
    			   				    				}
    			   				    				if(dbfind.ca_asstype){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_asstype,value:cateasstype}]);
    			   				    				}
    			   				    				if(dbfind.ca_assname){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_assname,value:cateassname}]);
    			   				    				}
    			   				    				if(dbfind.ca_level){
    			   				    					trigger.ownerCt.getForm().setValues([{id:dbfind.ca_level,value:calevel}]);
    			   				    				}
    			   				    				
    			   				    			});
    		   				    			} else {
    		   				    				trigger.setValue(catecode);
    		   				    			}
    		   				    		}
    		   				    		trigger.fireEvent('aftertrigger', trigger, data);
    		   				    		Ext.getCmp('cate-dbwin').hide();
    		   				    	}
    		   				    },{
    		   				    	text : '关  闭',
    		   				    	iconCls: 'x-button-icon-close',
    		   				    	cls: 'x-btn-gray',
    		   				    	handler : function(){
    		   				    		Ext.getCmp('cate-dbwin').hide();
    		   				    	}
    		   				    }]
    		   				});
       			    		dbwin.add({
       			    		    tag : 'iframe',
       			    		    frame : true,
       			    		    anchor : '100% 100%',
       			    		    layout : 'fit',
       			    		    html : '<iframe id="iframe_dbfind_'+caller+"_"+key+"="+keyValue+'" src="'+basePath+'jsps/common/catetreepaneldbfind.jsp?key='+key+"&dbfind="+dbfind+"&caller1="+caller+"&keyValue="+keyValue+"&trigger="+trigger.id+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
       			    		});
    				   	}
    				   	dbwin.show();
    				   },
    				   setMultiData: function(data) {
    					   var me = this;
    					   if(!this.ownerCt){
    						   var grid = trigger.owner;
    						   var record = grid.selModel.lastSelected;
    						   Ext.each(data, function(item, index){
    							   if(index > 0){
    								   record = me.next(grid, record);
    							   }
    							   if(record) {
    								   if(item && item.data){
    									   Ext.Array.each(Ext.Object.getKeys(item.data), function(k){
    										   Ext.Array.each(grid.dbfinds,function(ds){
	   				    		        			if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
	   				    		        				if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {
		   				    		            			record.set(ds.field, item.data[k]);
		   				    		            		}
	   				    		        			}
	   				    		            	});
	   				    		        	});
  				    					}
				    				}
				    			});
    					   } else {
    						   var val = [];
    						   Ext.each(data, function(item, index){
    							   val.push(item.raw.data.ca_code);
    						   });
    						   this.setValue(val.join('#'));
    					   }
    					   this.fireEvent('aftertrigger', this, data);
    				   },
    				   /**
    				    * 递归grid的下一条
    				    */
    				   next: function(grid, record){
    						record = record || grid.selModel.lastSelected;
    						if(record){
    							//递归查找下一条，并取到数据
    							var d = grid.store.getAt(record.index + 1);
    							if(d){
    								return d;
    							} else {
    								if(record.index + 1 < grid.store.data.items.length){
    									this.next(grid, d);
    								}
    							}
    						}
    					},
    				   autoDbfind: function(which, caller, field, condition){
    					   var me = this;
    					   Ext.Ajax.request({
    					   		url : basePath + 'common/autoDbfind.action',
    					   		params: {
    					   			which: which,
    					   			caller: caller,
    					   			field: field,
    					   			condition: condition
    					   		},
    					   		async: false,
    					   		method : 'post',
    					   		callback : function(options,success,response){
    					   			var res = new Ext.decode(response.responseText);
    					   			if(res.exceptionInfo){
    					   				showError(res.exceptionInfo);return;
    					   			}
    					   			if(res.data){
    					   				var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
    					   				me.autoSetValue(data[0], res.dbfinds || me.owner.dbfinds);
    					   			} else {
    					   				me.onTriggerClick();
    					   			}
    					   		}
    					   });
    				   },
    				   autoSetValue: function(data, dbfinds){
    					   var trigger = this;
    					   var triggerV = null;
    					   if(!trigger.ownerCt){//如果是grid的dbfind
    				    		var grid = trigger.owner;
    				    		var record = grid.selModel.lastSelected;//detailgrid里面selected
    				    		Ext.Array.each(Ext.Object.getKeys(data),function(k){
    				        		Ext.Array.each(dbfinds,function(ds){
    				        			if(ds.trigger == trigger.name ||Ext.isEmpty(ds.trigger)) {
    				        				if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {//k == ds.dbGridField//支持多dbgrid的字段对应grid同一字段
        				            			if(ds.field == trigger.name){
        				            				triggerV = data[k];//trigger所在位置赋值
        				            			}
        				            			record.set(ds.field, data[k]);
        				            		}
    				        			}
    				            	});
    				        	});
    				    	} else {
    				    		var ff;
    							Ext.Array.each(Ext.Object.getKeys(data),function(k){
    								Ext.Array.each(dbfinds,function(ds){
    									if(k == ds.dbGridField) {
    										if(Ext.getCmp(ds.field)){
    											if(trigger.name == ds.field){
    												triggerV = data[k];
    											} else {
    												ff = Ext.getCmp(ds.field);
    												if(ff && ff.setValue)
    													ff.setValue(data[k]);
    											}
    										}
    									}
    								});
    							});
    				    	}
    				    	trigger.setValue(triggerV);
    				    	data.data = data;
    				    	trigger.fireEvent('aftertrigger', trigger, data);
    				    	trigger.lastTriggerId = null;
    				    }
    				});