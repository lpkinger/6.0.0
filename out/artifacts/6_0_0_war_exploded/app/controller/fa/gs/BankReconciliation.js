Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BankReconciliation', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['fa.gs.BankReconciliation','core.form.Panel', 'core.grid.Panel2', 'core.form.SeparNumber',
            'core.button.Confirm', 'core.button.Cancel', 'core.button.Close', 'core.button.Query', 'core.button.AutoCheck'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpQueryButton': {
    			click: function(btn) {
    				me.query();
    			}
    		},
    		'field[name=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			}
    		},
    		'field[name=checkstatus]': {
    			change: function(f){
    				var form = f.up('form'), confBtn = form.down('erpConfirmButton'),
    					cancBtn = form.down('erpCancelButton');
    				if(f.value == '未对账'){
    					confBtn.setDisabled(false);
    					cancBtn.setDisabled(true);
    				} else if (f.value == '已对账') {
    					confBtn.setDisabled(true);
    					cancBtn.setDisabled(false);
    				} else {
    					confBtn.setDisabled(true);
    					cancBtn.setDisabled(true);
    				}
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		//自动对账
    		'erpAutoCheckButton': {
    			click: function(btn) {
    				warnMsg("确认自动对账吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gs/autoCheck.action',
    	    			   		params: {
    	    			   			yearmonth: Ext.getCmp('yearmonth').value,
    	    			   			caller : caller
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				me.query();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		//确认对账
    		'erpConfirmButton': {
    			afterrender: function(btn){
    				btn.setText( $I18N.common.button.erpConfirmCheckButton );
    				btn.setWidth( 100 );
    				var status = Ext.getCmp('checkstatus');
    				if(status && status.value == '未对账'){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			},
    			click: function(btn){
    				warnMsg("确定对账吗?", function(btn){
    					if(btn == 'yes'){
    	    				var grid1 = Ext.getCmp('grid1'), grid2 = Ext.getCmp('grid2');
    	    				var form = Ext.getCmp('form'), 
    	    					acdebit = Ext.getCmp('acdebit').value, accredit = Ext.getCmp('accredit').value,
    	    					ardebit = Ext.getCmp('ardebit').value, arcredit = Ext.getCmp('arcredit').value;
    	    				if(form.BaseUtil.numberFormat(acdebit-accredit, 2)!= form.BaseUtil.numberFormat(ardebit-arcredit, 2)){
	    						showError('银行对账单勾选的总金额【' + form.BaseUtil.numberFormat(acdebit-accredit, 2) + '】与银行登记勾选的总金额【' + form.BaseUtil.numberFormat(ardebit-arcredit, 2) + '】不相等！');
	    						return;
	    					}
    	    				me.FormUtil.getActiveTab().setLoading(true);//loading...
    	       				var items1 = grid1.selModel.getSelection(),
    	       					items2 = grid2.selModel.getSelection();
    	       				var data1 = Ext.Array.map(items1, function(item){
    	       					return {acd_id: item.get('ACD_ID')}; 
    	       				}), data2 = Ext.Array.map(items2, function(item){
    	       					return {ar_id: item.get('AR_ID')}; 
    	       				});
    	       				if(data1.length > 0 || data2.length > 0) {
    	       					Ext.Ajax.request({
    	       				   		url : basePath + 'fa/gs/confirmCheck.action',
    	       				   		params: {
    	       				   			data1: Ext.JSON.encode(data1).toString(),
    	       				   			data2: Ext.JSON.encode(data2).toString()
    	       				   		},
    	       				   		method : 'post',
    	       				   		callback : function(options,success,response){
    	       				   			me.FormUtil.getActiveTab().setLoading(false);
    	       				   			var localJson = new Ext.decode(response.responseText);
	    	       				   		if(localJson.exceptionInfo){
	    	    			   				showError(localJson.exceptionInfo);
	    	    			   			}
	    	    		    			if(localJson.success){
	    	    		    				me.query();
	    	       				   		}
    	       				   		}
    	       					});
    	       				} else {
    	       					showError("请勾选需要的明细!");
    	       				}
    					}
    				});
    			}
    		},
    		//取消对账
    		'erpCancelButton': {
    			afterrender: function(btn){
    				btn.setText( $I18N.common.button.erpCancelCheckButton );
    				btn.setWidth( 100 );
    				var status = Ext.getCmp('checkstatus');
    				if(status && status.value == '已对账'){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			},
    			click: function(btn){
    				warnMsg("取消对账吗?", function(btn){
    					if(btn == 'yes'){  						
    	    				var grid1 = Ext.getCmp('grid1'), grid2 = Ext.getCmp('grid2');
    	    				var form = Ext.getCmp('form'), 
    	    					acdebit = Ext.getCmp('acdebit').value, accredit = Ext.getCmp('accredit').value,
    	    					ardebit = Ext.getCmp('ardebit').value, arcredit = Ext.getCmp('arcredit').value;
    	    				if(form.BaseUtil.numberFormat(acdebit-accredit, 2)!= form.BaseUtil.numberFormat(ardebit-arcredit, 2)){
	    						showError('银行对账单勾选的总金额【' + form.BaseUtil.numberFormat(acdebit-accredit, 2) + '】与银行登记勾选的总金额【' + form.BaseUtil.numberFormat(ardebit-arcredit, 2) + '】不相等！');
	    						return;
	    					}
    	    				me.FormUtil.getActiveTab().setLoading(true);//loading...
    	       				var items1 = grid1.selModel.getSelection(),
    	       					items2 = grid2.selModel.getSelection();
    	       				var data1 = Ext.Array.map(items1, function(item){
    	       					return {acd_id: item.get('ACD_ID')}; 
    	       				}), data2 = Ext.Array.map(items2, function(item){
    	       					return {ar_id: item.get('AR_ID')}; 
    	       				});
    	       				if(data1.length > 0 || data2.length > 0) {
    	       					Ext.Ajax.request({
    	       				   		url : basePath + 'fa/gs/cancelCheck.action',
    	       				   		params: {
    	       				   			data1: Ext.JSON.encode(data1).toString(),
    	       				   			data2: Ext.JSON.encode(data2).toString()
    	       				   		},
    	       				   		method : 'post',
    	       				   		callback : function(options,success,response){
    	       				   			me.FormUtil.getActiveTab().setLoading(false);
    	       				   			var localJson = new Ext.decode(response.responseText);
	    	       				   		if(localJson.exceptionInfo){
	    	    			   				showError(localJson.exceptionInfo);
	    	    			   			}
	    	    		    			if(localJson.success){
	    	    		    				me.query();
	    	       				   		}
    	       				   		}
    	       					});
    	       				} else {
    	       					showError("请勾选需要的明细!");
    	       				}
    					}
    				});
    			}
    		}
    	});
    },
    query: function() {
    	var me = this;
		var grid1 = Ext.getCmp('grid1'), grid2 = Ext.getCmp('grid2');
		grid1.store.removeAll();
		grid2.store.removeAll();
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + 'fa/gs/getBankReconciliation.action',
	   		params: {
	   			caller: 'BankReconciliation',
	   			yearmonth: Ext.getCmp('yearmonth').value,
	   			status: Ext.getCmp('checkstatus').value,
	   			catecode: Ext.getCmp('cacode').value
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var res = new Ext.decode(response.responseText);
	   			var data1 = res.data1, data2 = res.data2;
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);
	   				return;
	   			}
				if(res.success){
					if(data1.length>0){
						grid1.store.loadData(data1);
					}
					if(data2.length>0){
						grid2.store.loadData(data2);
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
    			type: 'MONTH-B'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    }
});