Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.SaleForecastChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','pm.mps.SaleForecastChange','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.button.ResSubmit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    		'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						me.FormUtil.autoDbfind(caller, 'sc_sfcode', main);
					}
    			}
    		},
    		'erpGridPanel2': { 
    			afterrender:function(grid){
    				var tabid = getUrlParam("tabid");
    				var condition = getUrlParam("formCondition");
    				if(tabid && condition==null){
    					var tab = parent.Ext.getCmp(tabid);
        				var datas = tab.detaildatas;
        					Ext.defer(function(){
        						for(var i=0;i<datas.length;i++){
                					var scd_detno = datas[i].data['sd_detno'],
                					scd_prodcode = datas[i].data['sd_prodcode'],
                					sd_qty = datas[i].data['sd_qty'],
                					sd_needdate = datas[i].data['sd_needdate'],
                					sd_enddate = datas[i].data['sd_enddate'],
                					sd_remark = datas[i].data['sd_remark'],
                					oldspec = datas[i].data['pr_spec'],
                					oldunit = datas[i].data['pr_unit'],
                					pr_detail=datas[i].data['pr_detail'];
                					if(i==0){
                    					Ext.getCmp('grid').getStore().loadData([{
                    						"scd_detno":scd_detno,
                    						"scd_pddetno":scd_detno,
                    						"scd_prodcode":scd_prodcode,
                    						"scd_newprodcode":scd_prodcode,
                    						"scd_oldqty":sd_qty,
                    						"scd_olddelivery":sd_needdate,
                    						"scd_oldenddate":sd_enddate,
                    						"scd_newdelivery":sd_needdate,
                    						"scd_newenddate":sd_enddate,
                    						"scd_newqty":sd_qty,
                    						"scd_remark":sd_remark,
                    						"oldname":pr_detail,
                    						"oldspec":oldspec,
                    						"oldunit":oldunit,
                    						"newname":pr_detail,
                    						"newspec":oldspec,
                    						"newunit":oldunit
                    					}]);
                					}else{
                    					Ext.getCmp('grid').getStore().loadData([{
                    						"scd_detno":scd_detno,
                    						"scd_pddetno":scd_detno,
                    						"scd_prodcode":scd_prodcode,
                    						"scd_newprodcode":scd_prodcode,
                    						"scd_oldqty":sd_qty,
                    						"scd_olddelivery":sd_needdate,
                    						"scd_oldenddate":sd_enddate,
                    						"scd_newdelivery":sd_needdate,
                    						"scd_newenddate":sd_enddate,
                    						"scd_newqty":sd_qty,
                    						"scd_remark":sd_remark,
                    						"oldname":pr_detail,
                    						"oldspec":oldspec,
                    						"oldunit":oldunit,
                    						"newname":pr_detail,
                    						"newspec":oldspec,
                    						"newunit":oldunit
                    					}],true);
                					}
        						}
            				},500);
        				}
    			},
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			},
    			reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						var sfcode = Ext.getCmp("sc_sfcode").value;
						if(!sfcode){
							showError("请先选择预测单!");
							return;
						}
    					detail += " and SaleForecast.sf_code = '"+sfcode+"'";
						me.GridUtil.autoDbfind(grid, 'scd_pddetno', detail);
					}
				}
    		},
    		'field[name=sc_sfcode]': {
				afterrender:function(f){
					f.setFieldStyle({
						'color': 'blue'
	 				});
	 				f.focusCls = 'mail-attach';
	 				var c = Ext.Function.bind(me.openInvoice, me);
	 				Ext.EventManager.on(f.inputEl, {
	 					mousedown : c,
	 					scope: f,
	 					buffer : 100
	 				});
				}
	    	},
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(this);
    			}
    		},
    		'dbfindtrigger[name=scd_pddetno]': {
    			afterrender: function(t){
    				t.gridKey = "sc_sfcode";
    				t.mappinggirdKey = "SaleForecast.sf_code";
    				t.gridErrorMessage = "请先选择预测单!";
    			}
    		},
    		'multidbfindtrigger[name=scd_pddetno]': {
    			afterrender: function(t){
    				t.gridKey = "sc_sfcode";
    				t.mappinggirdKey = "SaleForecast.sf_code";
    				t.gridErrorMessage = "请先选择预测单!";
    			}
    		},
    		'dbfindtrigger[name=sc_sfcode]':{
    			afterrender:function(t){
    			  var code=getUrlParam('code');
    			  if(code) t.setValue(code);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
    		 	   	Ext.Array.each(items, function(item){
    				   if(!Ext.isEmpty(item.data['scd_prodcode'])){
    					   if(Ext.isEmpty(item.data['scd_newdelivery'])){
    						   item.set('scd_newdelivery', Ext.getCmp('scd_olddelivery'));
    					   }
    					   if(Ext.isEmpty(item.data['scd_newenddate'])){
    						   item.set('scd_newenddate', Ext.getCmp('scd_oldenddate'));
    					   }
    				   }
    			   });
    			   if(bool)
    				   this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSaleForecastChange', '新增销售预测变更单', 'jsps/pm/mps/saleForecastChange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sc_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
    		 	   	Ext.Array.each(items, function(item){
    				   if(!Ext.isEmpty(item.data['scd_prodcode'])){
    					   if(!Ext.isEmpty(item.data['scd_prodcode'])){
        					   if(Ext.isEmpty(item.data['scd_newdelivery'])){
        						   item.set('scd_newdelivery', Ext.getCmp('scd_olddelivery'));
        					   }
        					   if(Ext.isEmpty(item.data['scd_newenddate'])){
        						   item.set('scd_newenddate', Ext.getCmp('scd_oldenddate'));
        					   }
        				   }
    				   	 }
    			   });
    			   if(bool)
    				   me.FormUtil.onSubmit(Ext.getCmp("sc_id").value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sc_statuscode");
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp("sc_id").value);
    			}
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sc_statuscode");
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp("sc_id").value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("sc_statuscode");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp("sc_id").value);
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('sc_code').value == null || Ext.getCmp('sc_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
 	   	Ext.Array.each(items, function(item){
		   if(!Ext.isEmpty(item.data['scd_prodcode'])){
			   if(Ext.isEmpty(item.data['scd_newdelivery'])){
				   item.set('scd_newdelivery', Ext.getCmp('scd_olddelivery'));
			   }
			   if(Ext.isEmpty(item.data['scd_newenddate'])){
				   item.set('scd_newenddate', Ext.getCmp('scd_oldenddate'));
			   }
		   }
	   });
	   if(bool)
		   me.FormUtil.beforeSave(me);
	},
	openInvoice: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#sc_sfid');
		if(i && i.value) {
			url = 'jsps/scm/sale/saleForecast.jsp?formCondition=sf_idIS' + i.value + '&gridCondition=sd_sfidIS' + i.value;
			openUrl(url);
		}
	}
});