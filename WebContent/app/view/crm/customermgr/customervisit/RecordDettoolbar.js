/**
 * 此toolbar用于明细表grid
 */	
Ext.define('erp.view.crm.customermgr.customervisit.RecordDettoolbar',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.recordDettoolbar',
		dock: 'bottom',
		requires: ['erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy',
		           'erp.view.core.button.Paste','erp.view.core.button.Up','erp.view.core.button.Down',
		           'erp.view.core.button.UpExcel'],
		initComponent : function(){ 
			Ext.apply(this,{//default buttons
				items: [{
					xtype: 'tbtext',
					id: 'PDrow'
				},'-',{
					xtype: 'erpAddDetailButton',
					id:'PddDetail'
				},'-',{
					xtype: 'erpDeleteDetailButton',
					id:'PdeleteDetail'
				},'-',{
					xtype: 'copydetail',
					id:'PcopyDetail'
				},'-',{
					xtype: 'pastedetail',
					id:'PpasteDetail'
				},'-',{
					xtype: 'updetail',
					id:'PupDetail'
				},'-',{
					xtype: 'downdetail',
					id:'PdownDetail'
				},'-']
			});
			/*if(gridCondition == ""){//如果grid无数据，即录入界面，从数据库取配置的button
				var me = this;
				Ext.Ajax.request({
			   		url : basePath + "common/gridButton.action",
			   		params: {
			   			caller: caller
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.exceptionInfo){
		    				showError(localJson.exceptionInfo);
		    			}
		    			if(localJson.buttons){
		    				var buttons = Ext.decode(localJson.buttons);
		    				var index = 6;
		    				Ext.each(buttons, function(btn){
		    					me.insert(++index, btn);
		    				});
		    				//me.add(buttons);
		    			}
			   		}
				});
			}*/
			this.callParent(arguments); 
		}
	});