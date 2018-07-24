Ext.define('erp.view.sys.sale.CurrencyPortal',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.currencyportal',
	id:'currencyportal',
	layout:'border',
	/*border: false, */
	bodyStyle : 'background:#ffffff',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	requires:['erp.view.sys.sale.CurrencyForm','erp.view.sys.sale.CurrencyGrid'],
	layout:'border',
	items: [{
			region: 'north',
			width: '60%',
			xtype: 'currencyform'
	},{
		region:'west',
		width:283,//'30%',
		xtype:'currencygrid'
	}],
	buttonAlign:'center',
	buttons:[{xtype:'button',text:'确认',
		handler:function(btn){
			var grid = Ext.getCmp('currencygrid');
			var form = Ext.getCmp('currencyform');
			var r = form.getValues();
			var data = grid.store.data.items;
			//grid.store.commit(true);
			console.log("ssss");
			console.log(grid.store);
			var gridjson= new Array();
			Ext.Array.each(data,function(d){
				d.commit();
				gridjson.push(d.data)
			});
			gridStore =  Ext.JSON.encode(gridjson);
			var formstore = unescape(escape(Ext.JSON.encode(r)));
			Ext.Ajax.request({
				url: basePath + 'ma/logic/saveCurrency.action',
				params:{
					gridstore : gridStore,
					formstore : formstore
				},
				method:'post',
				timeout: 360000,
				callback: function(options,success,response) {
						var res = new Ext.decode(response.responseText);;
						if(res.success){
							var gris=document.getElementById('progress');
							Ext.Ajax.request({//拿到form的items
			    				url : basePath + "common/saas/common/checkData.action",
			    				params: {"table":table,"value":newvalue},
			    				method : 'post',
			    				callback : function  (options, success, response){
			    					var lis=document.getElementById('progress').getElementsByTagName('li');
			    					var res = new Ext.decode(response.responseText);
			    					if(res.res==true){
			    						for(var x=0;x<initabled.length;x++){
			    							if(initabled[x].VALUE==newvalue){
			    								initabled[x].INITABLED=1;
			    							}
			    						}
			    						for(var i=0;i<lis.length;i++){
			    						if(lis[i].getAttribute("value")==newvalue){
				    						 lis[i].getElementsByTagName('span')[0].setAttribute("class","bluebackground");
			    						}
			    						btn.hide();
			    					}
			    				}else{
			    					/*showResult("提示",newhtml+"数据为空,初始化失败!");*/
			    				 }
			    			}
						});
							showResult('提示','保存成功');
						}else{
							showResult('提示',res.exceptionInfo);
							return;
						}
				}
			});
		}}],
	initComponent : function(){ 
		var me = this; 
		me.callParent(arguments); 
} 
});