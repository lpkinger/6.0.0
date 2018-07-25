Ext.QuickTips.init();
Ext.define('erp.controller.common.JprocessAbnormal', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    //RenderUtil:Ext.create('erp.util.RenderUtil'),
    views:[
     		'scm.product.datalist.Viewport','scm.product.datalist.GridPanel','scm.product.datalist.Toolbar','core.button.VastAudit','core.button.VastDelete',
     		'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.button.Add','core.grid.YnColumn'
     	],
    init:function(){
    	this.control({
    		'erpDatalistGridPanel': { 
    			itemclick: this.onGridItemClick 
    		},
    		'erpAddButton':{
    			 afterrender: function(btn){
    				btn.ownerCt.insert(11,{
    				fieldLabel:'制单日期',	
    				xtype:'datefield',
    				labelAlign:'right',
    				id:'recorddate',
    				fieldStyle:'background:#FFFAFA;color:#515151;'
    				});
    				btn.setText('生成数据');
    				btn.setWidth(100);
    			},
    			click:function(btn){
    				var date=Ext.getCmp('recorddate').getValue();
    				Ext.Ajax.request({
    					params:{
    						date:date
    					},
    					url:basePath+'process/createAbnormalData.action',
    					method:'post',
    					callback:function(success,options,response){
    						var local=Ext.decode(response.responseText);
    						if(local.success){
    							Ext.Msg.alert('提示','数据生成成功!',function(){
    								Ext.getCmp('grid').getCount(caller,condition);
    							});	
    						}else if(local.exceptionInfo){
    							showError(local.exceptionInfo);
    						}else Ext.Msg.alert('提示','数据生成失败!');
    					}
    					
    				});
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
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
    		var value = record.data[keyField];
        	var formCondition = keyField + "IS" + value ;
        	var gridCondition = pfField + "IS" + value;
        	var panel = Ext.getCmp(caller + keyField + "=" + value); 
        	var main = parent.Ext.getCmp("content-panel");
        	if(!panel){ 
        		var title = "";
    	    	if (value.toString().length>4) {
    	    		 title = value.toString().substring(value.toString().length-4);	
    	    	} else {
    	    		title = value;
    	    	}
    	    	var myurl = '';
    	    	if(me.BaseUtil.contains(url, '?', true)){
    	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	} else {
    	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	}
    	    	myurl += "&datalistId=" + main.getActiveTab().id;
    	    	main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
    	    	panel = {       
    	    			title : me.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    	    			closable : true,
    	    			listeners : {
    	    				close : function(){
    	    			    	main.setActiveTab(main.getActiveTab().id); 
    	    				}
    	    			} 
    	    	};
    	    	this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
        	}else{ 
    	    	main.setActiveTab(panel); 
        	} 
    	}
    }, 
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	/*var tab = main.getComponent(o); */
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    },
    getCurrentStore: function(value){
    	var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    }
});