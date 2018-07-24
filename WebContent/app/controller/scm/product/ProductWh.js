Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductWh', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.ProductWh','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
    			'core.button.ResForBidden',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				//不能删除[已审核]的产品
    				var status = this.FormUtil.getFieldValue(caller, form.statusField, form.codeField + '=' + Ext.getCmp(form.codeField).value);
    				if(status != '105'){//非已审核
    					showError('不能删除[已审核]的产品!');return;
    				}
    				//不能删除已有采购单的物料
    				var bool = this.FormUtil.checkFieldValue('PurchaseDetail', 'pd_prodcode=' + Ext.getCmp(form.codeField).value);
    				if(!bool){
    					showError('不能删除已有采购单的物料!');return;
    				}
    				//不能删除已有采购计划的物料
    				var bool = this.FormUtil.checkFieldValue('PurchasePlan', 'pp_prodcode=' + Ext.getCmp(form.codeField).value);
    				if(!bool){
    					showError('不能删除已有采购计划的物料!');return;
    				}
    				//不能删除已有订单的物料
    				var bool = this.FormUtil.checkFieldValue('SaleDetail', 'sd_prodcode=' + Ext.getCmp(form.codeField).value);
    				if(!bool){
    					showError('不能删除已有订单的物料!');return;
    				}
    				//不能删除已使用在BOM清单的物料
    				var bool = this.FormUtil.checkFieldValue('BOMDetail', 'bd_soncode=' + Ext.getCmp(form.codeField).value 
    						+ ' AND bd_mothercode=' + Ext.getCmp(form.codeField).value);
    				if(!bool){
    					showError('不能删除已使用在BOM清单的物料!');return;
    				}
    				//产品已发生出入库,不能删除
    				var bool = this.FormUtil.checkFieldValue('ProdIODetail', 'pd_prodcode=' + Ext.getCmp(form.codeField).value);
    				if(!bool){
    					showError('产品已发生出入库,不能删除!');return;
    				}
    				this.FormUtil.onDelete([]);
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				//在物料规格或名称中,不能出现单引号
					var pr_detail = Ext.getCmp('pr_detail').value;
					var pr_spec = Ext.getCmp('pr_spec').value;
					if(me.BaseUtil.contains(pr_detail, "'", true) || me.BaseUtil.contains(pr_spec, "'", true)){
						showError('在物料规格或名称中,不能出现单引号!不能审核');return;
					}
					
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				var form = getForm(btn);
    				//只能对已审核的物料进行反审核
    				var status = this.FormUtil.getFieldValue(caller, form.statusField, form.codeField + '=' + Ext.getCmp(form.codeField).value);
    				if(status != '105'){//非已审核
    					showError('只能对已审核的物料进行反审核!');return;
    				}
    			}
    		},
    		'erpForBiddenButton': {
    			click: function(){
    				//pr_status='已禁用'
    			}
    		},
    		'erpResForBiddenButton': {
    			click: function(){
    				//只能对已禁用的物料进行反禁用
    				var status = this.FormUtil.getFieldValue(caller, form.statusField, form.codeField + '=' + Ext.getCmp(form.codeField).value);
    				if(status != '106'){//非已禁用
    					showError('只能对已禁用的物料进行反禁用!');return;
    				}
    				//pr_status='未审核'
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});