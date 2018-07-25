Ext.QuickTips.init();
Ext.define('erp.controller.pm.plm.Make', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','pm.plm.Make','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.Get',
  			'core.button.GetMaterial','core.button.DeleteMaterial','core.button.ReplaceMaterial','core.button.ChangeMaterial','core.button.GetCraft',
  			'core.button.CalMake','core.button.Check','core.button.ResCheck', 'core.button.End', 'core.button.ResEnd','core.button.ModifyMaterial','core.button.SubRelation'
  			],
    init:function(){
    	var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'ENTERING' ){
    					grid.setReadOnly(true);
    				}
    			},
    			itemclick: function(view,record){
    				me.itemclick(view,record,me);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					item.set('mm_code', Ext.getCmp('ma_code').value);
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['mm_oneuseqty'] == null || item.data['mm_oneuseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['mm_detno'] + "行未填写制单数量，或需求为0");return;
    						}
    						if(item.data['mm_whcode'] == null || item.data['mm_whcode'] == ''){
    							bool = false;
    							showError("明细第" + item.data['mm_detno'] + "行未选择发料仓库");return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'ENTERING' ){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item){
    					item.set('mm_code', Ext.getCmp('ma_code').value);
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						if(item.data['mm_oneuseqty'] == null || item.data['mm_oneuseqty'] == 0){
    							bool = false;
    							showError("明细第" + item.data['mm_detno'] + "行未填写制单数量，或需求为0");return;
    						}
    						if(item.data['mm_whcode'] == null || item.data['mm_whcode'] == ''){
    							bool = false;
    							showError("明细第" + item.data['mm_detno'] + "行未选择发料仓库");return;
    						}
    					}
    				});
    				if(bool){
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakePLM', '新增试产制造单', 'jsps/pm/plm/make.jsp?whoami=' + caller);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'UNAPPROVED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var grid = Ext.getCmp('grid');
    				for(var i=0;i<grid.store.data.items.length;i++){
    					var item = grid.store.data.items[i];
    					if(item.mm_totaluseqty!=0 ||item.mm_havegetqty!=0){
    						btn.hide();
    						break;
    					}
    				}
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要反审核单据?', function(btn) {
						if(btn == 'yes') {
							me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
						}
					});
    			}
    		},
    		'erpGetMaterialButton':{
    			afterrender: function(btn){
    				btn.hide();//暂时不启用
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpDeleteMaterialButton':{
    			afterrender: function(btn){
    				btn.hide();//暂时不启用
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpReplaceMaterialButton':{
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpGetCraftButton':{
    			afterrender: function(btn){
    				btn.hide();//暂时不启用
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpChangeMaterialButton': {
    			afterrender: function(btn){
    				btn.hide();//暂时不启用
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpCalMakeButton': {
    			click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要计算用料?', function(btn) {
						if(btn == 'yes') {
							var code = Ext.getCmp('ma_code').getValue();
							if (code) {
								Ext.Ajax.request({//拿到grid的columns
									url: basePath + 'pm/make/setMakeMaterial.action',
									params: {
										code: code
									},
									method: 'post',
									async: true,
									callback: function (options, success, response) {
										var res = new Ext.decode(response.responseText);
										if (res.exceptionInfo) {
											showError(res.exceptionInfo); return;
										}
										else {
											var grid = Ext.getCmp('grid');
											var value = Ext.getCmp('ma_id').value;
											var gridCondition = grid.mainField + '=' + value;
											gridParam = { caller: 'Make!PLM', condition: gridCondition };
											me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
										};
										//	console.log(this);
										//this.FormUtil.onUpdate(); 
									}
								});
							}
						}
					});
    			}
    		},
    		'erpSubRelationButton':{
    			click:function(btn){
    				var id=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["mm_id"];
    				var formCondition="mm_id IS"+id;
    				var gridCondition="mp_mmid IS"+id;
    				var linkCaller='MakeBase!Sub';
    				 var win = new Ext.window.Window(
    							{  
    								id : 'win',
    								height : '90%',
    								width : '95%',
    								maximizable : true,
    								buttonAlign : 'center',
    								layout : 'anchor',
    								items : [ {
    									tag : 'iframe',
    									frame : true,
    									anchor : '100% 100%',
    									layout : 'fit',
    									 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/make/makeCommon.jsp?whoami='+linkCaller+'&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    								} ],

    							});
    					win.show(); 
    			},
    			afterrender:function(btn){
    				btn.setDisabled(true);
    			}
    		},
    		'erpModifyMaterialButton':{
    			click:function(btn){
    				var id=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["mm_id"];
    				var formCondition="mm_id IS"+id;
    				var linkCaller='MakeMaterial!Modify';    				
    				 var win = new Ext.window.Window(
 							{  
 								id : 'win',
 								height : '90%',
 								width : '95%',
 								maximizable : true,
 								buttonAlign : 'center',
 								layout : 'anchor',
 								items : [ {
 									tag : 'iframe',
 									frame : true,
 									anchor : '100% 100%',
 									layout : 'fit',
 									 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/pm/make/modifyForm.jsp?whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
 								} ],
                                listeners:{
                                  'beforeclose':function(view ,opt){
                                	   //grid  刷新一次
                                	  var grid=Ext.getCmp('grid');
                                	  var gridParam = {caller: caller, condition: gridCondition};
                                	  grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                                	  Ext.getCmp('SubRelation').setDisabled(true);
                                  	  Ext.getCmp('ModifyMaterial').setDisabled(true);
                                  }	
                                	
                                }
 							});
 					win.show(); 
    			},
    			afterrender:function(btn){
    				btn.setDisabled(true);
    				
    			}
    			
    		},
    		'dbfindtrigger[name=ma_saledetno]': {
    			afterrender: function(t){
    				t.dbKey = "ma_salecode";
    				t.mappingKey = "sd_code";
    				t.dbMessage = "请先选择订单编号!";
    			}
    		},
    		'dbfindtrigger[name=mm_prodcode]': {
    			focus: function(t){
    				var grid = Ext.getCmp('grid');
    				var c = null;
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['mm_prodcode'] != null && item.data['mm_prodcode'] != ''){
    						if(c == null){
        						c = "(pr_code<>'" + item.data['mm_prodcode'] + "'";
        					} else {
        						c += " and pr_code<>'" + item.data['mm_prodcode'] + "'";
        					}
    					}
    				});
    				if(c != null){
    					t.dbBaseCondition = c + ")";
    				}
    			}
    		},
			'textfield[name=ma_wccode]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var d = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('mm_wccode',d);
						});
					}
				}
    		},
    		'erpCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onCheck(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResCheckButton': {
    			afterrender: function(btn){
    				var grid = Ext.getCmp('grid');
    				for(var i=0;i<grid.store.data.items.length;i++){
    					var item = grid.store.data.items[i];
    					if(item.mm_totaluseqty!=0 ||item.mm_havegetqty!=0){
    						btn.hide();
    						break;
    					}
    				}
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResCheck(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('ma_id').value);
    			}
    		},
    		'field[name=ma_prodcode]': {
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					me.FormUtil.getFieldValue('BOM', 'bo_id', "bo_mothercode='" + f.value + "'", 'ma_bomid');
    				}
    			}
    		},
    		'field[name=ma_qty]': {
    			change: function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				var grid = Ext.getCmp('grid');
					if(grid){
						var items = grid.store.data.items;
						Ext.each(items, function(item){//制单需求=制单套数*单位用量
							if(item.data['mm_oneuseqty'] != null && item.data['mm_oneuseqty'] != 0){
								item.set('mm_qty', item.data['mm_oneuseqty']*f.value);
							}
						});
					}
    			}
    		},
    		'field[name=mm_oneuseqty]': {
    			/*试产工单单位用量和需求数大多不成比例，不自动需求数 zyl
    			change: function(f){//制单需求=制单套数*单位用量
    				if(f.value != null && f.value > 0 && Ext.getCmp('ma_qty') && Ext.getCmp('ma_qty').value > 0){
    					var record = Ext.getCmp('grid').selModel.getLastSelected();
    					if(record.data['mm_qty'] != f.value*Ext.getCmp('ma_qty').value){
    						record.set('mm_qty', f.value*Ext.getCmp('ma_qty').value);
    					}
    				}
    			}*/
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    itemclick:function(view,record,me){
    	var show=0;
    	me.GridUtil.onGridItemClick(view,record);
    	//处理弹出界面的问题 
    	var grid=view.ownerCt;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    		var fieldValue=record.data[field];
           if(fieldValue==undefined||fieldValue==""||fieldValue==null){
        	   show=1;
        	   return; 
           }
        });
    	if(show==1){
    	Ext.getCmp('SubRelation').setDisabled(true);
    	Ext.getCmp('ModifyMaterial').setDisabled(true);
    	}else {
    		Ext.getCmp('SubRelation').setDisabled(false);
    		Ext.getCmp('ModifyMaterial').setDisabled(false);
    		}
    }
});