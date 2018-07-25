Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.CancelProdInOut', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views: [
        'core.form.CancelForm', 'scm.reserve.CancelProdInOut', 'core.grid.CancelGrid', 'core.toolbar.Toolbar', 'core.form.MultiField', 'core.form.YnField', 'core.form.SpecialContainField',
        'core.form.ConDateField','core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger', 'core.trigger.TextAreaTrigger','core.form.FileField','core.grid.YnColumn', 'core.button.PrintPDF'
    ],
    init: function() {
        var me = this;
        var grid = Ext.getCmp('cancelgrid');
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.control({
        	'erpCancelFormPanel':{
        		afterrender:function(f){
        			Ext.getCmp('Voucher').hide();
        		}
        	},
            'erpCancelGridPanel': {
                afterrender: function(grid) {
                    grid.plugins[0].on('beforeedit', function(args) {
                        if (args.field == "pd_inqty") {
                            return me.isAllowUpdateQty(args.record);
                        }
                        if (caller == 'ProdInOut!SaleAppropriationOut' || 'ProdInOut!OtherOut' == caller) {
                            if (args.field == "pd_ordercode") {
                                return me.isAllowSale(args.record);
                            }
                            if (args.field == "pd_plancode") {
                                return me.isAllowForeCast(args.record);
                            }
                        }
                    });
                    if (caller == 'ProdInOut!SaleReturn') {
                        if (Ext.getCmp('pi_sourcecode') && Ext.getCmp('pi_sourcecode').value != "") {
                            grid.readOnly = true;
                        }
                    }
                },cellclick:function(view,td,cellIndex,record,tr,rowIndex,e,eOpts){
                	//商城来源的订单不允许修改某些列的值
    				var bindCode = "pd_ordercode,pd_orderdetno,pd_prodcode";
    				var field = view.ownerCt.columns[cellIndex].dataIndex;
			    	if(Ext.getCmp('pi_ordertype') && Ext.getCmp('pi_ordertype').value == "B2C"){
						if(bindCode.indexOf(field)>=0) return false;
					}
		        },
                itemclick: function(selModel, record) {
                    var bool = me.hasSource(selModel.ownerCt);
                    if (caller == 'ProdInOut!OtherIn' || caller == 'ProdInOut!OtherPurcIn') {
                        if (record.data.pd_id > 0) {
                            var btn = selModel.ownerCt.down('#erpEditSubpackageButton');
                            if (btn && !btn.hidden)
                                btn.setDisabled(false);
                        }
                    }
                    if (caller == 'ProdInOut!Sale' || caller == 'ProdInOut!SaleReturn') {
                        if (record.data.pd_id > 0) {
                            var btn = Ext.getCmp('UpdateProdInoutrateButton');
                            if (btn && !btn.hidden)
                                btn && btn.setDisabled(false);
                        }
                        if(Ext.getCmp('pi_ordertype') && Ext.getCmp('pi_ordertype').value == "B2C"){
							bool=true;//商城订单出货单不允许添加新行
						}
                    }
                    if (caller == 'ProdInOut!PurcCheckout') {
                        if (record.data.pd_id > 0 && Ext.getCmp('pi_statuscode').value != 'POSTED' && Ext.isEmpty(record.data.pd_ordercode)) {
                            var btn = selModel.ownerCt.down('#erpGetPurcPrice');
                            if (btn && !btn.hidden)
                                btn.setDisabled(false);
                        }
                    }
                    if (record.data.pd_id != 0 && record.data.pd_id != null && record.data.pd_id != '') {
                        var btn = Ext.getCmp('updatebgxh');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpSaveOrdercodeButton');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('barcodebtn');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpUpdateDetailWHCode');
                        btn && btn.setDisabled(false);
                        btn = Ext.getCmp('erpUpdateBatchCode');
                        btn && btn.setDisabled(false);
						btn = Ext.getCmp('splitdetail');
						btn && btn.setDisabled(false);
                        if (Ext.getCmp('pi_statuscode').value != 'POSTED' && (!Ext.isEmpty(record.data.pd_pocode) || !Ext.isEmpty(record.data.pd_ordercode))) {
                            btn = Ext.getCmp('catchBatchByOrder');
                            btn && btn.setDisabled(false);
                        }
                    }
                    
                    if (!bool)
                        this.GridUtil.onGridItemClick(selModel, record);
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            }
        })
    }
});