Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.InternalOffsetMain', {
	extend : 'Ext.app.Controller',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views : ['fa.gla.InternalOffsetMain','core.form.Panel','core.grid.Panel2','core.form.MonthDateField','ma.bench.TabPanel',
	         'core.button.Close','core.button.Query','core.button.CatchData'],
	init : function() {
		var me = this;
		this.control({
			'tabpanel':{
				afterrender: function(tab){
					me.FormUtil.getActiveTab().setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'fa/gla/getInternalOffsets.action',
    			   		params: {
    			   			yearmonth: Ext.getCmp('yearmonth').value
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
    		    					var url = 'jsps/fa/gla/internalOffset.jsp?mastercode=' + node.ss_mastercode + '&yearmonth=' + Ext.getCmp('yearmonth').value + '&_noc=1';
    		    					if(node.io_id){
    		    						url += '&formCondition=io_idIS'+node.io_id+'&gridCondition=iod_ioidIS'+node.io_id;
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
    				me.getCurrentMonth(f);
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
    				warnMsg("确认清空当前各子账套合并抵消数据，重新获取？", function(btn){
    					if(btn == 'yes'){
    						var yearmonth = Ext.getCmp('yearmonth');
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gla/autoCatchInternalOffset.action',
    	    			   		params: {
    	    			   			yearmonth: yearmonth.value,
    	    			   			currency: Ext.getCmp('currency').value
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
					panel.fireEvent('activate', panel, true);
			}
		}
    }
});