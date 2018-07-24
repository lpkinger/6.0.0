Ext.QuickTips.init();
Ext.define('erp.controller.hr.kbi.KBIbill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.kbi.KBIbill','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger',
  			'core.button.Assess'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				// itemclick: this.onGridItemClick
			},
			 'dbfindtrigger[name=kbd_criterion]': {
	                focus: function(t) {
	                    t.setHideTrigger(false);
	                    t.setReadOnly(false);
	                    var record = Ext.getCmp('grid').selModel.getLastSelected();
	                    var code = record.data['kbd_ksid'];
	                    if (code == null || code == '') {
	                        t.setHideTrigger(true);
	                        t.setReadOnly(true);
	                        return;
	                    } else {
	                        t.dbBaseCondition = "ksd_ksid='" + code + "'";
	                    }
	                }
	            },
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					// 保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('kb_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKBIbill', '新增KBI考评人申请', 'jsps/hr/kbi/kBIbill.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kb_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('kb_id').value);					
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kb_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('kb_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kb_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('kb_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kb_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('kb_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpAssessButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('kb_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.assess();
				}
			},
			'erpFormPanel':{
				afterrender:function(form){
					form.onSumitSuccess=function(){
						if (parent && parent.Ext.getCmp('content-panel')) {						
							var firstGrid = parent.Ext.getCmp('content-panel').items.items[0].firstGrid;
							if (firstGrid && firstGrid != null) {
								firstGrid.loadNewStore();
							}
						}
					};
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    assess:function(){
    	if(Ext.getCmp('assessWin')){
    		Ext.getCmp('assessWin').show();
    		Ext.each(Ext.getCmp('assesstabpanel').items.items,function(tab,index){
    			tab.hasResult=false;
    		});
    		var tabpanel=Ext.getCmp('assesstabpanel');
    		tabpanel.fireEvent('tabchange',tabpanel,tabpanel.activeTab);
    		return;
    	}
    	var me=this;
    	var itemss=new Array();
    	var keys=this.getKeys();
    	Ext.each(keys,function(name,index){
    		var item=new Object();
    		item.title=keys[index];
    		item.key=keys[index];
    		item.xtype= 'form';
    		item.autoScroll= true;
    		item.loaded=false;
    		item.hasResult=false;
        	itemss.push(item);
    	});
    	itemss[0].items=new Array();
    	itemss[0].loaded=true;
    	itemss[0].hasResult=true;
    	var assessValues=this.getAssessValue(keys[0]);
    	var store = Ext.getCmp('grid').getStore();
    	Ext.Object.each(assessValues,function(key,value){
    		var it=new Object();
    		it.xtype='fieldset';
     		it.title='<h2>'+key+'</h2>';
    		it.collapsible=true;
    		it.items=new Array();
    		Ext.each(value,function(name,index){
    			var i=new Object();
    			i.xtype= 'radiofield';
		        i.name= key;
		        i.value= value[index][1];
		        i.score= value[index][1];
		        i.boxLabel= value[index][0];
		        store.each(function(gitem){
		        	if(gitem.data.kbd_target==i.name&&gitem.data.kbd_score==i.score){
		        		i.checked=true;
		        		return false;
		        	}
		        });
    			it.items.push(i);
    		});
    		itemss[0].items.push(it);
    	});
    	var win=Ext.create('Ext.window.Window',{
			width: 800,
			id:'assessWin',
	       	height: 500,
	       	autoScroll: true,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>评 估</h1>',
	       	items:[{
	       		   id:'assesstabpanel',
	       	       xtype:'tabpanel',
	       	       listeners:{
	       	    	tabchange:function(tab,newCard){
	       	    		if(!newCard.loaded){	
	       	    			var store = Ext.getCmp('grid').getStore();
	       	    			var assessValues=me.getAssessValue(newCard.key);
	       	    			Ext.Object.each(assessValues,function(key,value){
	       	    	    		var it=new Object();
	       	    	    		it.xtype='fieldset';
	       	    	     		it.title='<h2>'+key+'</h2>';
	       	    	    		it.collapsible=true;
	       	    	    		it.items=new Array();
	       	    	    		Ext.each(value,function(name,index){
	       	    	    			var i=new Object();
	       	    	    			i.xtype= 'radiofield';
	       	    			        i.name= key;
	       	    			        i.value= value[index][1];
	       	    			        i.score= value[index][1];
	       	    			        i.boxLabel= value[index][0];
	       	    			        store.each(function(gitem){
	       	    			        	if(gitem.data.kbd_target==i.name&&gitem.data.kbd_score==i.score){
	       	    			        		i.checked=true;
	       	    			        		return false;
	       	    			        	}
	       	    			        });
	       	    	    			it.items.push(i);
	       	    	    		});
	       	    	    		newCard.add(it);
	       	    	    	});
	       	    			newCard.loaded=true;
	       	    			newCard.hasResult=true;
	       	    		}
	       	    		if(!newCard.hasResult){
	       	    			var store=Ext.getCmp('grid').getStore();
	       	    			Ext.each(newCard.items.items,function(item){
	       	    				Ext.each(item.items.items,function(i){
	       	    					store.each(function(gitem){
	       	    			        	if(gitem.data.kbd_target==i.name&&gitem.data.kbd_score==i.score){
	       	    			        		i.setValue(true);
	       	    			        		return false;
	       	    			        	}
	       	    			        });
	       	    				});
	       	    			});
	       	    			newCard.hasResult=true;
	       	    		}
	       	    	}
	       	       },
					items:itemss
	       	}],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'确定',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.setResult(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
    	win.show();
    },
    getKeys:function(){
    	var data=new Object();
    	Ext.Ajax.request({
  		   url : basePath +'hr/kbi/getKeys.action',
  		   method : 'post',
  		   async: false,
  		   params:{
  			   caller:caller
  		   },
  		   callback : function(options,success,response){
  			   var rs = new Ext.decode(response.responseText);
  			   if(rs.exceptionInfo){
  				   showError(rs.exceptionInfo);return;
  			   }
  			   data=rs.data;
  		   }
  	   });
    	return data;
    },
    getAssessValue:function(key){
    	var data=new Object();
    	Ext.Ajax.request({
 		   url : basePath +'hr/kbi/getAssessValue.action',
 		   method : 'post',
 		   async: false,
 		   params:{
 			   key:key
 		   },
 		   callback : function(options,success,response){
 			   var rs = new Ext.decode(response.responseText);
 			   if(rs.exceptionInfo){
 				   showError(rs.exceptionInfo);return;
 			   }
 			   data=rs.data;
 		   }
 	   });
    return data;
    },
    setResult:function(win){
    	var values=new Array();
    	var forms=win.items.items[0].items.items;
    	Ext.each(forms,function(name,index){
    		if(name.hasResult){
    			var fieldset=forms[index].items.items;
        		if(fieldset){
        			Ext.each(fieldset,function(nam,inde){
        				var f=fieldset[inde].items.items;
        				if(f){
        					Ext.each(f,function(na,ind){
        	    				if(f[ind]&&f[ind].checked){
        	    					var o=new Object();
        	    					o.key=f[ind].name;
        	    					o.criterion=f[ind].boxLabel;
        	    					o.score=f[ind].score;
        	    					values.push(o);
        	    				}
        	        		});
        				}
            		});
        		}
    		}
    		
    	});
    	var grid=Ext.getCmp('grid');
    	var store=grid.getStore();
    	Ext.each(values,function(name,index){
    		store.each(function(item){
    			if(values[index].key==item.data.kbd_target){
    				item.set('kbd_criterion',values[index].criterion);
    				item.set('kbd_score',values[index].score);
    			}
    		});
    	});
    }
});