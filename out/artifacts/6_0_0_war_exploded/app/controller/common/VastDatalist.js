Ext.QuickTips.init();
Ext.define('erp.controller.common.VastDatalist', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.vastDatalist.Viewport','common.vastDatalist.GridPanel','core.grid.YnColumn',
     		'common.vastDatalist.Toolbar','core.button.VastDelete','core.button.VastPost',
     		'core.button.VastClose','core.button.VastApprove','core.button.VastReStart','core.button.VastFreeze',
     		'core.button.VastSend','core.button.VastAudit','core.button.VastSubmit',
     		'core.button.VastSave','core.button.VastSpare','core.button.VastSimulate',
     		'core.button.VastWriexam','core.button.VastInterview','core.button.VastJointalcpool',
     		'core.button.VastWritemark','core.button.VastIntermark','core.button.VastTurnfullmemb',
     		'core.button.VastTurnover','core.button.VastTurnCaree','core.button.Turnfullmemb',
     		'core.button.TurnPosition','core.button.TurnCaree','core.button.VastSocailaccount',
     		'core.button.VastSocailsecu','core.button.VastGet','core.button.VastSendOut',
     		'core.button.AgreeToPrice','core.button.AgreeAllToPrice','core.button.NotAgreeToPrice',
     		'core.button.Sync','scm.purchase.inquiryVastDatalist.GridPanel','scm.purchase.inquiryVastDatalist.Viewport','scm.purchase.inquiryVastDatalist.Toolbar'
     	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpVastDeleteButton': {//btn.url在GridButton里面配置
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastDelete.action');
    			}
    		},
    		'erpVastCloseButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastClose.action');
    			}
    		},
    		'erpVastReStartButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastResStart.action');
    			}
    		},
    		'erpVastFreezeButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastFreeze.action');
    			}
    		},
    		'erpVastSendButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastSend.action');
    			}
    		},
    		'erpVastAuditButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastAudit.action');
    			}
    		},
    		'erpVastSubmitButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastSubmit.action');
    			}
    		},
    		'erpVastSaveButton': {
    			click: function(btn){
    				me.vastSave(btn.url || 'common/vastSave.action');
    			}
    		},
    		'erpVastSpareButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastSpare.action');
    			}
    		},
    		'erpVastSimulateButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastSimulate.action');
    			}
    		},
    		'erpVastWriexamButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/vastWriexam.action');
    			}
    		},
    		'erpVastInterviewButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/vastInterview.action');
    			}
    		},
    		'erpVastJointalcpoolButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/vastJointalcpool.action');
    			}
    		},
    		'erpVastWritemarkButton':{
    			click: function(btn){
    				me.vastMark(btn.url || 'hr/emplmana/vastWritemark.action','re_mark');
    			}
    		},
    		'erpVastIntermarkButton':{
    			click: function(btn){
    				me.vastMark(btn.url || 'hr/emplmana/vastIntermark.action','re_intermark');
    			}
    		},
    		'erpVastSocailaccountButton':{
    			click: function(btn){
    				me.vastMark(btn.url || 'hr/emplmana/vastSocailaccount.action','em_accumucard');
    			}
    		},
    		'erpVastSocailsecuButton':{
    			click: function(btn){
    				me.vastMark(btn.url || 'hr/emplmana/vastSocailsecu.action','em_socailcard');
    			}
    		},
    		'erpVastTurnfullmembButton':{
    			click: function(btn){
    				me.vastDealreload(btn.url || 'hr/emplmana/vastTurnfullmemb.action');
    			}
    		},
    		'erpVastTurnoverButton':{
    			click: function(btn){
    				me.vastDealreload(btn.url || 'hr/emplmana/vastTurnover.action');
    			}
    		},
    		'erpVastTurnCareeButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/vastTurnCaree.action');
    			}
    		},
    		'erpTurnfullmembButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/turnfullmemb.action');
    			}
    		},
    		'erpTurnPositionButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/turnPosition.action');
    			}
    		},
    		'erpTurnCareeButton':{
    			click: function(btn){
    				me.vastDeal(btn.url || 'hr/emplmana/turnCaree.action');
    			}
    		},
    		'erpVastGetButton': {
    			click: function(btn){
    				me.vastSave(btn.url || 'common/vastSave.action');
    			}
    		},    		
    		'erpVastSendOutButton': {
    			click: function(btn){
    				me.vastSave(btn.url || 'common/vastSave.action');
    			}
    		},
    		'erpAgreeToPriceButton': {
    			click: function(btn){
    				me.vastDealreload(btn.url || 'common/vastAgreeTurnPrice.action?_noc=1');
    			}
    		},    		
    		'erpNotAgreeToPriceButton': {
    			click: function(btn){
    				me.vastDeal(btn.url || 'common/vastNotAgreeTurnPrice.action?_noc=1');
    			}
    		},
    		'button[id=closebutton]':{
    			afterrender:function(btn){
    			 btn.ownerCt.ownerCt.getButtons();
    			}
    		},
    		'erpSyncButton': {
    			afterrender: function(btn) {
    				btn.caller = caller;
    				var grid = btn.ownerCt.ownerCt;
    				if(typeof keyField !== 'undefined') {
    					grid.selModel.on('selectionchange', function(sel, selected){
        					var ids = [];
        					Ext.Array.each(selected, function(){
        						ids.push(this.get(keyField));
        					});
        					btn.syncdatas = ids.join(',');
        				});
    				}
    			}
    		}
    	});
    },
    vastDealreload: function(url){
    	var grid = Ext.getCmp('grid');
		var records = grid.getMultiSelected();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel")||parent.parent.parent.Ext.getCmp("content-panel");//win内嵌iframe
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: {
		   			caller: caller,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					window.location.reload();
		   					/*main.getActiveTab().close();*/
		   				});
		   			}
		   		}
			});
		}
    },    
    vastDeal: function(url){
    	var grid = Ext.getCmp('grid');
		var records = grid.getMultiSelected();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel")||parent.parent.parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: {
		   			caller: caller,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					window.location.reload();
		   					/*main.getActiveTab().close();*/
		   				});
		   			}
		   		}
			});
		}
    },
    vastMark: function(url,field){
    	var grid = Ext.getCmp('grid');
		var records = grid.multiselected;
		if(records.length > 0){
			var id = new Array();
			var mark = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
				mark[index] = record.data[field];
			});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: {
		   			caller: caller,
		   			id: id,
		   			mark:mark
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					main.getActiveTab().close();
		   				});
		   			}
		   		}
			});
		}
    },
    vastSave: function(url){
    	var grid = Ext.getCmp('grid');
    	var data = grid.getEffectData();
    	if(data.length > 0){
    		var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: {
		   			caller: caller,
		   			data: Ext.encode(data)
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					window.location.href = window.location.href;
		   				});
		   			}
		   		}
			});
    	}
    }
});