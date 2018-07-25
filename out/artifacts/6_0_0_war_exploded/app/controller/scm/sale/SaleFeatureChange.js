Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleFeatureChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.sale.SaleFeatureChange','core.grid.Panel2','core.toolbar.Toolbar',
     		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
 			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
 			'core.button.ResSubmit','core.form.FileField',
 			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
 	],
 	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					Ext.each(items,function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			                if (item.data['scd_newqty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			                }
			                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
			                	if (Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新交货日期小于当前日期');
				                    return;
				                }
			                }
			                if(!Ext.isEmpty(item.data['scd_newenddate'])){
				                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于当前日期');
				                    return;
				                }
				                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
					                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d')) {
					                    bool = false;
					                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于新截止日期');
					                    return;
					                }
				                }
			                }
			            }
       				});
       				if(bool){
       					this.FormUtil.beforeSave(this);
       				}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sfc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					Ext.each(items,function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			                if (item.data['scd_newqty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			                }
			                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
			                	if (Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新交货日期小于当前日期');
				                    return;
				                }
			                }
			                if(!Ext.isEmpty(item.data['scd_newenddate'])){
				                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于当前日期');
				                    return;
				                }
				                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
					                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d')) {
					                    bool = false;
					                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于新截止日期');
					                    return;
					                }
				                }
			                }
			            }
       				});
       				if(bool){
						this.FormUtil.onUpdate(this);
       				}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSaleFeatureChange', '新增销售特征值变更单', 'jsps/scm/sale/saleFeatureChange.jsp?whoami=' + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sfc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items, bool = true;
					Ext.each(items,function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			                if (item.data['scd_newqty'] > item.data['scd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['scd_detno'] + '行的新数量大于原数量');
			                    return;
			                }
			                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
			                	if (Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新交货日期小于当前日期');
				                    return;
				                }
			                }
			                if(!Ext.isEmpty(item.data['scd_newenddate'])){
				                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
				                    bool = false;
				                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于当前日期');
				                    return;
				                }
				                if(!Ext.isEmpty(item.data['scd_newdelivery'])){
					                if (Ext.Date.format(item.data['scd_newenddate'],'Y-m-d') < Ext.Date.format(item.data['scd_newdelivery'],'Y-m-d')) {
					                    bool = false;
					                    showError('明细表第' + item.data['scd_detno'] + '行的新截止日期小于新截止日期');
					                    return;
					                }
				                }
			                }
			            }
       				});
       				if(bool){
						me.FormUtil.onSubmit(Ext.getCmp('sfc_id').value);
       				}
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sfc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sfc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sfc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sfc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sfc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sfc_id').value);
				}
			},
			'erpPrintButton': {
				click:function(btn){
				var reportName="SaleFeatureChange";
				var condition='{SaleFeatureChange.sfc_id}='+Ext.getCmp('sfc_id').value+'';
				var id=Ext.getCmp('sfc_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
			},
    		'dbfindtrigger[name=sfcd_orderdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['sfcd_ordercode'];
    				var grid = Ext.getCmp('grid');
    				if(code == null || code == ''){
    					showError("请先选择订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    				var cond ="(sa_code='"+code+"' ";
    				var arr = new Array();
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['sfcd_ordercode'] != null && item.data['sfcd_ordercode'] != ''
    						&& item.data['sfcd_orderdetno'] != null && item.data['sfcd_orderdetno'] !=''){
    						if(item.data['sfcd_ordercode'] ==code){
    							arr.push("sd_detno<>"+item.data['sfcd_orderdetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
    			}
    			/*aftertrigger:function(){
    				var grid = Ext.getCmp('grid');
    				var datas = new Array();
    				Ext.each(grid.store.data.items, function(item){
    					console.log(item);
    					datas.push();
    				});
    			}*/
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});