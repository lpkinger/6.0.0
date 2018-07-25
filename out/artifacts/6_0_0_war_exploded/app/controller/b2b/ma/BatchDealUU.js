/**
 * 批量获取供应商、客户UU号
 */
Ext.QuickTips.init();
Ext.define('erp.controller.b2b.ma.BatchDealUU', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['b2b.ma.BatchDealUU','core.button.Close'],
    init: function(){
    	this.control({
    		'button[id=deal]': {
    			afterrender: function(f) {
     				this.getDealDetail(f);
    			},
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt;
    				this.deal(form);
    			}
    		}
    	});
    },
    getDealDetail: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'b2b/vastCountUU.action',
    		params:null,
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				Ext.getCmp('grid').getStore().loadData([{
    					item: '客户',
    					type: 'customer',
    					checkedcount:rs.data.customer.checkedcount,
    					nocheckedcount:rs.data.customer.nocheckedcount,
    					success: 0,
    					failure: 0
    				},{
    					item: '供应商',
    					type: 'vendor',
    					checkedcount:rs.data.vendor.checkedcount,
        				nocheckedcount:rs.data.vendor.nocheckedcount,
        				success: 0,
    					failure: 0
    				}]);
    			} else if(rs.exceptionInfo) {
    				showMessage('提示', rs.exceptionInfo);
    			}
    		}
    	});
    },
    getVendorDetail: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'b2b/vastCountVendorUU.action',
    		params:null,
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data);
    			}
    		}
    	});
    },
    deal: function(form, ym) {
    	var tab = this.FormUtil.getActiveTab();
    	tab.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'b2b/vastCheckUU.action',
    		timeout: 4800000,
    		callback: function(opt, s, r) {
    			tab.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if(rs.success) {
    				Ext.getCmp('grid').getStore().loadData([{
    					item: '客户',
    					type: 'customer',
    					checkedcount:rs.data.customer.checkedcount,
    					nocheckedcount:rs.data.customer.nocheckedcount,
    					success: rs.data.customer.success,
    					failure: rs.data.customer.failure
    				},{
    					item: '供应商',
    					type: 'vendor',
    					checkedcount:rs.data.vendor.checkedcount,
        				nocheckedcount:rs.data.vendor.nocheckedcount,
        				success: rs.data.vendor.success,
    					failure: rs.data.vendor.failure
    				}]);
    			} else if(rs.exceptionInfo) {
    				showMessage('提示', rs.exceptionInfo);
    			}
    		}
    	});
    }
});