Ext.define('erp.view.common.main.FlowPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpFlowPanel',
	defaults: { 
		autoScroll:true, 
		/*bodyPadding: 5,*/
		cls: 'my-panel'
	}, 
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: true,
	layout: 'accordion',
	border: false,
	autoShow: true,
	collapsible : true, 
	plain: true,
	initComponent : function(){
		this.callParent(arguments); 
	},
	listeners:{
		activate:function (tab){
			var items=tab.getItems(tab);
			if(items.length==0){
				var title="设置个人流程";
				var url="jsps/common/PersonalProcessSet.jsp";
				items.push({
					title:'查看流程',
					frame:true,
					html:'<h2>你还没有设置个人导航流程或系统没有定义相应的导航图</br><a style="text-decoration:none;" class="x-btn-link" onclick="openTable('+'\''+title+'\',\''+url+'\''+ ');">快去看看吧!</a></h2>'
				});
			}
			if(tab.items.length==0){
				tab.add(items);
			}else {
				tab.removeAll();
				tab.add(items);
			}

		}
	},
	getItems:function(panel){
		var items=new Array();
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'common/PersonalProcess.action',
			method : 'post',
			async:false,
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var bool=res.data.length>5?true:false;
				var arr= [{
					type: 'search',
					tooltip:'添加导航图',
					handler:function(){
						Ext.create('erp.util.BaseUtil').onAdd('PersonalProcessSet','个人流程设置','jsps/common/PersonalProcessSet.jsp');
					}
				},{
					type:'refresh',
					tooltip: '刷新导航图',
					handler:function(){
						panel.fireEvent('activate',panel);
					}
				}];
				if(bool){
					arr.push({
						type:'expand',
						tooltip: '更多导航图',
						handler:function(){
							if(!Ext.getCmp('moreflow-win')){
								Ext.create('Ext.window.Window', {
					    			id: 'moreflow-win',
					    			title: '更多导航图',
					    			height: screen.height*0.8,
					        		width: screen.width*0.2,
					    			renderTo: Ext.getBody(),
					    			animCollapse: false,
					                constrainHeader: true,
					                bodyBorder: true,
					                layout: 'accordion',
					                border: false,
					                autoShow: true,
					                collapsible : true, 
					                x: 0,
					                items:panel.getMoreItems(res.data)
					    		});
							} else {
								Ext.getCmp('moreflow-win').show();
							}
						}
					});
				}
				Ext.Array.each(res.data,function(item,index){  
					if(index>4) return; 
					var url='workfloweditor/flownavigationonlyeditor.jsp?jdId='+item.id;
					var tag=new Object();
					if(index==0){
						tag={
								xtype:'panel',
								title : item.name,
								border : false,
								tools:arr,
								html : '<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',   	
								listeners:{
									expand:function(p,opts){
										if(p.body.dom.firstChild==null){
											p.body.insertHtml('afterBegin','<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',false ) ;	
										}	
									}
								}          
						};
					}else {					
						tag={
								xtype:'panel',
								title : item.name,
								listeners:{
									expand:function(p,opts){
										if(p.body.dom.firstChild==null){
											p.body.insertHtml('afterBegin','<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',false ) ;	
										}	
									}
								}               
						};
					}					
					items.push(tag);
				});
			}
		});
		return items;
	},
	getMoreItems:function(items){
		var moreitems=new Array();
		Ext.Array.each(items,function(item,index){
			var tag=new Object();
			var url='workfloweditor/flownavigationonlyeditor.jsp?jdId='+item.id;
			if(index>4){
			if(index==5){
				tag={
						xtype:'panel',
						title : item.name,
						border : false,
						html : '<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',   	
						listeners:{
							expand:function(p,opts){
								if(p.body.dom.firstChild==null){
									p.body.insertHtml('afterBegin','<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',false ) ;	
								}	
							}
						}          
				};	
			}else if(index>5){
				tag={
						xtype:'panel',
						title : item.name,
						listeners:{
							expand:function(p,opts){
								if(p.body.dom.firstChild==null){
									p.body.insertHtml('afterBegin','<iframe id="iframe_jprocess_' + item.id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',false ) ;	
								}	
							}
						}               
				};
			}
			moreitems.push(tag);
			}
		});
		return moreitems;
	}
	

});