Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.ConsolidatedMain', {
	extend : 'Ext.app.Controller',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views : ['fa.gla.ChildReportMain','core.form.Panel','core.grid.Panel2','core.form.MonthDateField','ma.bench.TabPanel',
	         'core.button.Close','core.button.CatchData','core.button.ReportAccount'],
	init : function() {
		var me = this;
		this.control({
    		'field[name=fatype]':{
    			change:function(field){
    				me.setActiveTab(field);
    			}
    		},
			'tabpanel':{
				afterrender: function(tab){
					me.FormUtil.getActiveTab().setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'fa/gla/getChildReports.action',
    			   		params: {
    			   			yearmonth: Ext.getCmp('yearmonth').value,
    			   			fatype: Ext.getCmp('fatype').value,
    			   			kind: '集团报表'
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				Ext.Array.each(localJson.children, function(node){
    		    					var url = 'jsps/fa/gla/consolidated.jsp?mastercode=' + node.ss_mastercode + '&yearmonth=' + Ext.getCmp('yearmonth').value + '&fatype=' + Ext.getCmp('fatype').value + '&_noc=1';
    		    					if(node.cr_id){
    		    						url += '&formCondition=cr_idIS'+node.cr_id+'&gridCondition=crd_cridIS'+node.cr_id;
    		    					}
	    		    				tab.add({ 
										title : node.ss_mastername,
										tag : 'iframe',
										id : node.ss_mastercode,
										tabConfig:{tooltip:node.ss_mastername},
										border : false,
										layout : 'fit',
										html : '<iframe id="iframe_add_base'+node.ss_mastercode+'" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
									});
    		    				});
    			   			}
    			   		}
    				});
				}
			},
			'field[name=yearmonth]': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			},
    			change:function(field){
    				me.setActiveTab(field);
    			}
    		},
    		'field[name=currency]': {
    			afterrender: function(f) {
    				this.getDefaultCurrency(f);
    			}
    		},
    		'erpCatchDataButton':{
    			click: function(b) {
    				warnMsg("确认清空当前各子账套报表数据，重新获取？", function(btn){
    					if(btn == 'yes'){
    						var yearmonth = Ext.getCmp('yearmonth');
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gla/autoCatchReport.action',
    	    			   		params: {
    	    			   			yearmonth: yearmonth.value,
    	    			   			currency: Ext.getCmp('currency').value,
    	    			   			fatype: Ext.getCmp('fatype').value,
    	    			   		    kind: '集团报表'    	    			  
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				me.setActiveTab(yearmonth);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpReportAccountButton':{
    			click: function(b) {
    				warnMsg("确认按照当前各账套报表数据，重新计算？", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gla/countConsolidated.action',
    	    			   		params: {
    	    			   			yearmonth: Ext.getCmp('yearmonth').value,
    	    			   			currency: Ext.getCmp('currency').value,
    	    			   			fatype: Ext.getCmp('fatype').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				me.setActiveTab(b);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
		})
	},
	getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		async: false,
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    getDefaultCurrency: function(f) {
    	this.BaseUtil.getSetting('sys', 'defaultCurrency', function(v){
    		if(v)
    			f.setValue(v);
    	});
    },
    setActiveTab:function(field){
		var tab = field.up('form').nextSibling();
			if(tab){
				var panel = tab.getActiveTab();
				if(panel){
					panel.fireEvent('activate',panel, true);
			}
		}
    }
});