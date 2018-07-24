
Ext.define('erp.view.sysmng.upgrade.version.VersionPanelPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.VersionPanelPanel',
	id: 'versionpanelpanel', 
	closeAction:'hide',
	//layout: "vbox",
	border:false,	
	title:'升级记录',	
	requires: [],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items:[{
					xtype : 'dataview',				
					itemId : 'Ubasicnav',
					id:'Ubasicnav',
					trackOver:true,
					autoHeight : true,
					autoScroll:false,				
					//overItemCls : 'x-view-over',
					//itemSelector : 'div.thumb-wrap',
					tpl :new Ext.XTemplate(
							'<tpl for=".">',
														
							'<div class="main">',
							'<div>【标识号】：{LOG_NUMID}</div>',
							'<div>【版本号】：{LOG_VERSION}</div>',
							'<div>【提交人】：{LOG_MAN}</div>',
							'<div>【操作时间】：{LOG_DATE}</div>',
							'<table>',
							'<tr><td class="upgradetd">【升级说明】：</td>', 
							'<td><div><html>{LOG_REMARK}</html></div></td><tr></table>',
							' </div>', 
									

							'</tpl>'),
							
							
					store : Ext.create('Ext.data.Store', {
						fields : [
									{name:'LOG_MAN', type:'string'},
									{name:'LOG_DATE', type:'data',convert:function(value){  
            							var CREATED = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
            							 return CREATED;  
         							} 
										
									},
									{name:'LOG_VERSION', type:'number'},
									{name:'LOG_REMARK',type: 'string'},
									{name:'LOG_NUMID', type:'string'}
								]	
						
					})
					
				}],
	initComponent : function(){ 
	
		this.callParent(arguments);
		//this.getSysUpgradeLog(1);
	},
	
	getSysUpgradeLog: function(id){
		me=this;
		var id={id:id};
		
		me.setLoading(true);
		  Ext.Ajax.request({
            	timeout: 5000,
                url : basePath + 'upgrade/getLog.action',
                params:id,
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    me.setLoading(false);
                    
                    if(res.log){
                    
					if(res.log==""){
						//var Ubasicnav=Ext.getCmp(Ubasicnav);
						document.getElementById('Ubasicnav').innerHTML="<div>暂时没有升级记录</div>";

					
					}else{
						 me.items.items[0].store.loadData(res.log)
					}
                   

                    } else if(res.exceptionInfo){
                        showError(res.exceptionInfo);
                    }
                }
            });
		
	}
	
	
});