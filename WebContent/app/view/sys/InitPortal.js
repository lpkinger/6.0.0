Ext.define('erp.view.sys.InitPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.syspanel', 
	id:'syspanel',
	layout:'card',
	bodyBorder: false,
	layout:'card',
	border: false,
	autoShow: true,
	bodyStyle:'background-color:#f1f1f1;',
	buttonAlign : 'left',
	buttons: [
	{
	  	xtype : 'tbtext',
	  	name : 'row',
	  	defaultAlign:'left',
	    cls:'baseconfirmtpl',
	  	id:'row',
	  	text: '请确认您的【企业信息】初始化工作已完成?',
	  	listeners:{
	    	 'afterrender':function(panel,opt){
	    		 	panel.hide();
	    	 	}
	    	 }
	 },
	{
		id: 'confirm',
		text: '确认', 
		width:'100px',
		region:'center',
		cls:'baseconfirmbutton',
		/*baseCls:'baseconfirmbutton',*/
		/*x:1000,
    	y:25,*/
		handler: function(btn) {
			/*Ext.Msg.confirm('提示', '确认完成['+newhtml+']初始化吗?',
					   function(choice) {
				   if(choice === 'yes') {*/
						var gris=document.getElementById('progress');
						Ext.Ajax.request({//拿到form的items
		    				url : basePath + "common/saas/common/checkData.action",
		    				params: {"table":table,"value":newvalue},
		    				method : 'post',
		    				callback : function  (options, success, response){
		    					var lis=document.getElementById('progress').getElementsByTagName('li');
		    					var res = new Ext.decode(response.responseText);
		    					if(res.res==true){
		    						showResult("提示",newhtml+"数据初始化完成!");
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
		    						Ext.getCmp('row').hide();
		    					}
		    				}else{
		    					showResult("提示","检测到"+newhtml+"数据为空,请重新检查您的数据！");
		    				 }
		    			}
					});   
				  /* }else{
					   return false;
				   }
			   }
			   ); */
			},
			listeners:{
		    	 'afterrender':function(panel,opt){
		    		 	panel.hide();
		    	 	}
		    	 }
		}
	],
	initComponent : function(){ 
		var me=this;
		Ext.applyIf(me,{
			items:me.getItems()
		});
		this.callParent(arguments);
	},
	ActiveIndex_:1,
	changeCard:function(panel,direction,index,panelFlag,importcaller,paneltitle){
		var layout = panel.getLayout();
		var app=erp.getApplication();	
		if(direction){
			layout[direction]();			
		}else{
			var a=index;
			layout.setActiveItem(index);
			this.ActiveIndex_=index;
		} 
		if(direction){
			this.ActiveIndex_=direction=='next'?++this.ActiveIndex_:--this.ActiveIndex_;
			 var lis=document.getElementById('progress').getElementsByTagName('li');
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	if(i==this.ActiveIndex_){
	    	 	 		if(lis[i].getElementsByTagName('span')[1].innerHTML=='开始'){
	    	 	 			lis[i].setAttribute("class","active");
	    	 	 		}else{
	    	 	 			lis[i].setAttribute("class","normal active");
	    	 	 		}
	    	 	 	}else{
	    	 	 		if(lis[i].getElementsByTagName('span')[1].innerHTML=='开始'){
	    	 	 			lis[i].setAttribute("class","start");
	    	 	 		}else{
	    	 	 			lis[i].setAttribute("class","normal");
	    	 	 		}
	    	 	 	}
	    	 	 }
		}
		activeItem=layout.getActiveItem();
		if(panelFlag){
			var panel=activeItem.child('defaultpanel');		
			if(!panel){
				panel =  Ext.widget('defaultpanel',{flag:panelFlag});
				activeItem.add(panel);
				activeItem.down('grid').getData(true);
			}else{
				activeItem.removeAll();
				panel =  Ext.widget('defaultpanel',{flag:panelFlag});
				activeItem.add(panel);
				activeItem.down('grid').getData(true);
			}
		}
		if(importcaller){
			var panel=activeItem.child('importpanel');	
			if(!panel){
				panel =  Ext.widget('importpanel');
				panel.add({
					tag : 'iframe',
					style:{
						background:'#f0f0f0',
						border:'none'
					},	
					anchor: '100% 100%',
					frame : true,
					border : false,
					layout : 'fit',
					height:window.innerHeight*0.9,
					html :'<iframe id="iframe_maindetail_" src="'+basePath+'jsps/sys/import.jsp?whoami='+importcaller
					+'&title='+paneltitle+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				});
				panel.doLayout();
				activeItem.add(panel);
			}else{
				panel.removeAll();
				panel.add({
					tag : 'iframe',
					style:{
						background:'#f0f0f0',
						border:'none'
					},						  
					frame : true,
					border : false,
					layout : 'fit',
					height:window.innerHeight*0.9,
					html :'<iframe id="iframe_maindetail_" src="'+basePath+'jsps/sys/import.jsp?whoami='+importcaller
					+'&title='+paneltitle+'"  height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
				});
				panel.doLayout();
				activeItem.add(panel);
			}
		}
		if(activeItem.type){
			var contrlPath=this.getContrlPath(activeItem.type);
			Ext.require("erp.controller."+contrlPath,function(){				
				var Controller = app.getController(contrlPath);//4.2 直接init
			},self);
		}
		Ext.getCmp('syspanel').setTitle('<center>'+paneltitle+'</center>');
	},
	getContrlPath:function(type){
		return "sys.step."+type+"Controller";
	},
	getItems:function(){
		var array=[/*{
			desc: '开始',
			xtype:'panel',
			bodyStyle : 'background:#F9F9F9;',
			html:'<br /><br /><br /><br /><br /><br /><br /><br /><br />' +
					'<div align="center" style="font-variant:normal; font-family:serif; font-size:12pt; font-weight:bold; font-style:normal; ">' +
					'欢迎使用优企云服！<br /><br />现在我们一起来完成使用系统前需要做的一些基础设置吧！</div>'
		},*/{
			desc: '企业信息',
			xtype:'enterpriseportal'
		},{
			desc: '组织人员',
			layout:'fit',
			type:'Hr'			
		},{
	 		desc:'岗位权限',	 		
			layout:'fit',
	 		type:'PR'			
	 	},
	 	{
	 		desc:'审批流',	 		
			layout:'fit',
	 		type:'Jp'
	 	},{
	 		desc:'基础资料(销售预测类型)',	
			layout:'fit'
	 	},{
	 		desc:'基础资料(导入)',	
			layout:'fit'
	 	},{
	 		desc:'基础资料(物料单位)',
			xtype:'combosetgrid',
			caller:'Product',
			field:'pr_unit'
		},{
	 		desc:'基础资料(物料种类)',
	 		layout:'fit',
	 		type:'Basic'
		},{
	 		desc:'基础资料(其它入库类型)',	
	 		xtype:'combosetgrid',
			caller:'ProdInOut!OtherIn',
			field:'pi_type'
			//layout:'fit',
	 		//type:'Basic'
	 	},
	 	{
	 		desc:'基础资料(其它出库类型)',	
	 		xtype:'combosetgrid',
	 		caller:'ProdInOut!OtherOut',
			field:'pi_type'
			//layout:'fit',
	 		//type:'Basic'
	 	},
	 	{
	 		desc:'基础资料(拨出单)',	
	 		xtype:'combosetgrid',
	 		caller:'ProdInOut!AppropriationOut',
			field:'pi_type'
			//layout:'fit',
	 		//type:'Basic'
	 	},{
		desc: '基础资料(币别)',
		xtype:'currencyportal'
	   }
		];
	return array;
	}
});