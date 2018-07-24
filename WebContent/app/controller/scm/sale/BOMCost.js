Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.BOMCost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.BOMCost','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.grid.YnColumn','core.button.BOMCost','core.button.BOMInsert','core.button.BOMVastCost',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.BOMPrint',
			'core.button.DeleteDetail'
		],
        init:function(){
        	var me = this;
    		this.control({
    			'erpGridPanel2': { 
        			itemclick: this.onGridItemClick
        		},
        		'erpDeleteDetailButton': {
	                afterrender: function(btn){
	                    btn.ownerCt.add({
	                        xtype: 'erpBOMPrintButton'
	                    });
	                }
            	},
        		'erpBOMPrintButton':{
	    			click:function(btn){
	    			var grid = Ext.getCmp('grid');
    				var reportName = "BOMCostView",
						id = grid.selModel.lastSelected.data["bcd_bomid"],
						prodcode=grid.selModel.lastSelected.data["bcd_prodcode"];
						callers = 'BOM!BOMCostDetail!Print';
    				if(id == "" || id == null){
    				   showError("请先选择需要打印成本的BOM");
    				   return;
    				}
					var condition = '{BOM.bo_id}=' + id+' and {BomStruct.bs_topmothercode}='+"'"+prodcode+"'";
					var thisreport="";
					Ext.Ajax.request({
				   		url : basePath + 'common/enterprise/getprinturl.action?caller=' + callers,
				   		callback: function(opt, s, r) {
				   			var re = Ext.decode(r.responseText);
				   			var thisreport=re.reportname;				   			
							var rpname = re.reportName;						
							if(thisreport==""||thisreport==null||thisreport=='null'){
								thisreport=reportName;
							}
				   		}
			   		});
					
					this.onwindowsPrintBom(id, thisreport, condition,prodcode);
    			},
	    			afterrender:function(btn){
	    				btn.setDisabled(true);
	    			}
    			},
    			'erpSaveButton': {
    				click: function(btn){  
    					var form = me.getForm(btn);
    					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    						me.BaseUtil.getRandomNumber();//自动添加编号
    					}
    					this.FormUtil.beforeSave(this);
    				}
    			}
        		,
    			'erpDeleteButton' : {
    				click: function(btn){
    					me.FormUtil.onDelete(Ext.getCmp('bc_id').value);
    				}
    			},
    			'erpUpdateButton': {
    				afterrender: function(btn){
        				var status = Ext.getCmp('bc_checkstatuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
    				click: function(btn){
    					this.FormUtil.onUpdate(this);
    				}
    			},
    			'erpAddButton': {
    				click: function(){
    					me.FormUtil.onAdd('addBOMCost', '新增BOM成本表', 'jsps/scm/sale/bomcost.jsp');
    				}
    			},
    			'erpCloseButton': {
    				click: function(btn){
    					me.FormUtil.beforeClose(me);
    				}
    			},
    			'erpSubmitButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'ENTERING'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onSubmit(Ext.getCmp('bc_id').value);
    				}
    			},
    			'erpResSubmitButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'COMMITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onResSubmit(Ext.getCmp('bc_id').value);
    				}
    			},
    			'erpAuditButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'COMMITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onAudit(Ext.getCmp('bc_id').value);
    				}
    			},
    			'erpResAuditButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'AUDITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onResAudit(Ext.getCmp('bc_id').value);
    				}
    			},
    			'erpPrintButton': {
    				click:function(btn){
    				var reportName="sale_gj";
    				var condition='{BOMCost.bc_id}='+Ext.getCmp('bc_id').value+'';
    				var id=Ext.getCmp('bc_id').value;
    				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    			},
    			'erpBannedButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value == 'DISABLE'){
    						btn.hide();
    					}
    				},
        			click: function(btn){
        				this.FormUtil.onBanned(Ext.getCmp('bc_id').value);
        			}
        		},
        		'erpResBannedButton': {
        			afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'DISABLE'){
    						btn.hide();
    					}
    				},
        			click: function(btn){
        				this.FormUtil.onResBanned(Ext.getCmp('bc_id').value);
        			}
        		},
        		'erpBOMInsertButton': {
        			afterrender: function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'ENTERING'){
    						btn.hide();
    					}
    				},
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					bc_id = Ext.getCmp('bc_id').value;
        				form.setLoading(true);
            			Ext.Ajax.request({
            				url: basePath + 'scm/sale/BOMCost/bominsert.action',
            				params: {
            					bc_id: bc_id
            				},
            				timeout: 600000,
            				callback: function(opt, s, r) {
            					form.setLoading(false);
            					var rs = Ext.decode(r.responseText);
            					if(rs.success) {
            						alert('导入成功!');
            						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'bcd_bcid=' + bc_id});
            					} else if(r.exceptionInfo) {
            						showError(r.exceptionInfo);
            					}
            				}
            			});
        			}
        		},
        		'erpBOMVastCostButton': {
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        				bc_id = Ext.getCmp('bc_id').value;
        				form.setLoading(true);
            			Ext.Ajax.request({
            				url: basePath + 'scm/sale/BOMCost/bomvastcost.action',
            				params: {
            					bc_id: bc_id
            				},
            				timeout: 600000,
            				callback: function(opt, s, r) {
            					form.setLoading(false);
            					var rs = Ext.decode(r.responseText);
            					if(rs.success) {
            						alert('计算完成!');
            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'bc_id=' + bc_id});
            						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'bcd_bcid=' + bc_id});
            					}else if(r.exceptionInfo) {
            						showError(r.exceptionInfo);
            					}
            				}
            			});
        			},
        			afterrender:function(btn){
    					var status = Ext.getCmp('bc_checkstatuscode');
    					if(status && status.value != 'ENTERING'){
    						btn.hide();
    					}
        			}
        		},
        		'textfield[name=bc_rate]':{
        			change: function(f){
        				var v = Ext.isEmpty(f.value) ? 1 : f.value,
        					grid = f.up('form').ownerCt.down('grid');
        				grid.store.each(function(){
        					this.set('evd_price', Ext.Number.toFixed(this.get('evd_doubleprice')/v, 6));
        					this.set('evd_amount', Ext.Number.toFixed(this.get('evd_price')*this.get('evd_qty'), 2));
        				});
        			}
        		}
    		});
    	},
    	onwindowsPrintBom: function(id, reportName, condition,prodcode){
			var me = this;
			var url = 'pm/bom/printBOM.action?caller=BOM!BOMCostDetail!Print';
			me.setLoading(true);//loading...		
			Ext.Ajax.request({
				url : basePath + url,
				params: {
					id: id,
					reportName:reportName,
					condition:condition,
					prodcode:prodcode
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo) {
						me.setLoading(false);
						showError(res.exceptionInfo);
						return;
					}
					Ext.Ajax.request({
						url : basePath + 'common/enterprise/getprinturl.action',
						params: {
							caller: callers,
							reportName: reportName
						},
						callback: function(opt, s, r) {
							me.setLoading(false);
							var re = Ext.decode(r.responseText);
							console.log(re);
							if(re.exceptionInfo) {
								showError(re.exceptionInfo);
								return;
							}
							if(re.printurl) {
								var whichsystem=re.whichsystem;
								var url = re.printurl + '?reportfile=' + 
								(re.report || res.keyData[0]) +'&&rcondition='+condition+'&&company=&&sysdate='+res.keyData[3]+'&&key='+res.keyData[1]+'&&whichsystem='+whichsystem+'';		
								window.open(url,'_blank');
							}
						}
					});
				}
			});
		},
		setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
			var mask = this.mask;
			if (!mask) {
				this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
					msg : "处理中,请稍后...",
					msgCls : 'z-index:10000;'
				});
			}
			if (b)
				mask.show();
			else
				mask.hide();
		},
    	onGridItemClick: function(selModel, record){//grid行选择
    		this.GridUtil.onGridItemClick(selModel, record);
    		Ext.getCmp('BOMPrint').setDisabled(false);
    	},
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
    });