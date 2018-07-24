Ext.QuickTips.init();
Ext.define('erp.controller.common.EditDatalist', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.editDatalist.Viewport','common.editDatalist.GridPanel','common.editDatalist.Toolbar','core.form.FtField',
     		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
     		'core.form.FtNumberField','core.form.MonthDateField'
     	],
    init:function(){
        this.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({
    		'erpEditDatalistGridPanel': { 
    			afterrender:function(grid){
    				if(Ext.isIE && !Ext.isIE11){
    					document.body.attachEvent('onkeydown', function(){
    						if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
    							var e = window.event;
    							if(e.srcElement) {
    								window.clipboardData.setData('text', e.srcElement.innerHTML);
    							}
    						}
    					});
    				} else {
    					grid.getEl().dom.addEventListener("mouseover", function(e){
        					if(e.ctrlKey){
        						 var Contextvalue=e.target.textContent==""?e.target.value:e.target.textContent;
        						 textarea_text = parent.document.getElementById("textarea_text");
        						 textarea_text.value=Contextvalue;
        					     textarea_text.focus();
        					     textarea_text.select();
        					}
        		    	});
    				}
    			}
    		},
    		'erpVastDeleteButton': {
    			click: function(btn){
    				var dlwin = new Ext.window.Window({
   			    		id : 'dlwin',
	   				    title: btn.text,
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   				    items: [{
	   				    	  tag : 'iframe',
	   				    	  frame : true,
	   				    	  anchor : '100% 100%',
	   				    	  layout : 'fit',
	   				    	  html : '<iframe id="iframe_dl_'+caller+'" src="'+basePath+'jsps/common/vastDatalist.jsp?urlcondition='+condition+'&whoami='+caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	   				    }],
	   				    buttons : [{
	   				    	text: btn.text,
	   				    	iconCls: btn.iconCls,
	   				    	cls: 'x-btn-gray-1',
	   				    	handler: function(){
	   				    		
	   				    	}
	   				    },{
	   				    	text : '关  闭',
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		Ext.getCmp('dlwin').close();
	   				    	}
	   				    }]
	   				});
	   				dlwin.show();
    			}
    		},
    		'erpRefreshButton': {
    			click: function(btn){
    				 Ext.Ajax.request({
     					url: basePath + (btn.url || 'fa/gs/copyAccountRegister/refreshQuery.action'),     					
     					params: {
     						
 						},
     					method: 'post',        					
     					callback: function(opt, s, r) {        						
     						var rs = Ext.decode(r.responseText);
     						if(rs.exceptionInfo){
				   				showError(rs.exceptionInfo);
				   				return "";
				   			}
			    			if(rs.success){
				   				Ext.Msg.alert("提示", "刷新成功!", function(){
				   					window.location.reload();
				   				});
				   			}
     					}
     				});
    			}
    		},
    		'erpVastSaveButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt,
    					data = grid.getEffectData();
    				if(data.length > 0) {
    					grid.setLoading(true);
    					Ext.Ajax.request({
    				   		url : basePath + (btn.url || 'common/vastSave.action'),
    				   		params: {
    				   			caller: caller,
    				   			data: Ext.encode(data)
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			grid.setLoading(false);
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    				   				showError(rs.exceptionInfo);
    				   				return "";
    				   			}
    			    			if(rs.success){
    				   				Ext.Msg.alert("提示", "保存成功!", function(){
    				   					window.location.reload();
    				   				});
    				   			}
    				   		}
    					});
    				}
    			}
    		},
    		'erpConfirmPeriodsButton': {
    			click: function(btn) {
    				warnMsg("确认要设置每个模块的当前期间?", function(b){
    					if(b == 'yes'){
		    				var grid = btn.ownerCt.ownerCt,
		    					data = grid.getEffectData();
		    				if(data.length > 0) {
		    					grid.setLoading(true);
		    					Ext.Ajax.request({
		    				   		url : basePath + (btn.url || 'common/vastConfirmPeriods.action'),
		    				   		params: {
		    				   			caller: caller,
		    				   			data: Ext.encode(data)
		    				   		},
		    				   		method : 'post',
		    				   		callback : function(options,success,response){
		    				   			grid.setLoading(false);
		    				   			var rs = new Ext.decode(response.responseText);
		    				   			if(rs.exceptionInfo){
		    				   				showError(rs.exceptionInfo);
		    				   				return "";
		    				   			}
		    			    			if(rs.success){
		    				   				Ext.Msg.alert("提示", "设置成功!", function(){
		    				   					window.location.reload();
		    				   				});
		    				   			}
		    				   		}
		    					});
		    				}
    					}
    				});
    			}
    		},
    		'erpVastGetButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt,
    					data = grid.getEffectData();
    				if(data.length > 0) {
    					grid.setLoading(true);
    					Ext.Ajax.request({
    				   		url : basePath + (btn.url || 'common/vastSave.action'),
    				   		params: {
    				   			caller: caller,
    				   			data: Ext.encode(data)
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			grid.setLoading(false);
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    				   				showError(rs.exceptionInfo);
    				   				return "";
    				   			}
    			    			if(rs.success){
    				   				Ext.Msg.alert("提示", "保存成功!", function(){
    				   					window.location.reload();
    				   				});
    				   			}
    				   		}
    					});
    				}
    			}
    		},
    		'erpVastSendOutButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt,
    					data = grid.getEffectData();
    				if(data.length > 0) {
    					grid.setLoading(true);
    					Ext.Ajax.request({
    				   		url : basePath + (btn.url || 'common/vastSave.action'),
    				   		params: {
    				   			caller: caller,
    				   			data: Ext.encode(data)
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			grid.setLoading(false);
    				   			var rs = new Ext.decode(response.responseText);
    				   			if(rs.exceptionInfo){
    				   				showError(rs.exceptionInfo);
    				   				return "";
    				   			}
    			    			if(rs.success){
    				   				Ext.Msg.alert("提示", "保存成功!", function(){
    				   					window.location.reload();
    				   				});
    				   			}
    				   		}
    					});
    				}
    			}
    		}
    	});
    } 
});