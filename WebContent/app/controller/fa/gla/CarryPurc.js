Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.CarryPurc', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['fa.gla.CarryPurc', 'core.button.Close'],
    init: function(){
    	this.control({
    		'displayfield[name=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'button[id=deal]': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt,
    					ym = form.down('#yearmonth').value,
    					account = form.down('#account').value;
    				if(btn.isUnCreate) {
    					this.unCreate(form, ym);
    				} else {
    					this.create(form, ym, account);
    				}
    			}
    		}
    	});
    },
    getCurrentMonth: function(f) {
    	var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    				me.getPurcVoucher(f.ownerCt.down('#vocode'), rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    getPurcVoucher: function(f, y) {
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'Voucher',
	   			field: 'vo_code',
	   			condition: 'vo_yearmonth=' + y + ' and instr(vo_explanation,\'结转采购费用\')>0'
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				f.hide();
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success && r.data) {
	   				f.setValue(r.data);
	   				var form = f.ownerCt;  
	   				form.down('#account').hide();
	   				form.down('#deal').setText('取消凭证');
	   				form.down('#deal').isUnCreate = true;
	   			} else {
	   				f.hide();
	   			}
	   		}
		});
    },
    create: function(form, ym, account) {
    	var tab = this.FormUtil.getActiveTab();
    	tab.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/vc/createVoucher.action',
    		params: {
    			vs_code: 'PurcFee',
    			mode: 'merge',
    			datas: 'pi_class in (\'采购验收单\',\'采购验退单\') and pi_statuscode=\'POSTED\' and to_char(pi_date,\'yyyymm\')=' + ym,
    			kind: '结转采购费用',
    			yearmonth: ym,
    			vomode: 'GL'
    		},
    		timeout: 4800000,
    		callback: function(opt, s, r) {
    			tab.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else {
    				if(rs.success && rs.content){
		   				var msg = "";
		   				Ext.Array.each(rs.content, function(item){
		   					if(item.errMsg) {
		   						msg += item.errMsg + '<hr>';
		   					} else if(item.id) {
		   						msg += '凭证号:<a href="javascript:openUrl2(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' 
	    							+ item.id + '&gridCondition=vd_voidIS' + item.id + '\',\'凭证\',\'vo_id\','+item.id+');">' + item.code + '</a><hr>';	
		   					}
		   				});
    					showMessage('提示', msg);
		   			}
    			}
    		}
    	});
    },
    unCreate: function(form, ym) {
    	var tab = this.FormUtil.getActiveTab();
    	tab.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/unCreatePurcfee.action',
    		params: {
    			yearmonth: ym
    		},
    		callback: function(opt, s, r) {
    			tab.setLoading(false);
    			var rs = Ext.decode(r.responseText);
    			if (rs.error) {
    				showError(rs.error);
    			} else if(rs.success) {
    				showMessage('提示', '成功取消采购费用凭证!', 1000);
    				window.location.reload();
    			}
    		}
    	});
    }
});