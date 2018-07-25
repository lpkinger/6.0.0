Ext.require([
    'erp.util.*'
]);
Ext.define('erp.view.common.DeskTop.DeskTabPanel',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.erpDeskTabPanel',//多列表tabpanel
	id:'desktabpanel',
	region: 'center', 
	activeTab: 0, 
	border: false, 
	animScroll:true,	//使用动画滚动效果
	layoutOnTabChange : true,	//随着布局变化
	resizeTabs:true, // turn on tab resizing
    enableTabScroll : true,	//tab标签超宽时自动出现滚动效果
	plain: true,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	listeners: {
		'add': function(t, p) {
			p.on('activate', function(){
				 var grid = t.activeTab;
				 	 caller=grid.caller;
				 	if(grid.firstPage){
				 		grid.firstPage=false;
				 		page=1;
				 	}
				 	else {
				 		page=grid.down('erpDatalistToolbar').child('#inputItem').getValue();
				 		}				 
					 grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
					 grid.getColumnsAndStore();						 
			});
			}
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	var keyField= this.activeTab.keyField;
    	var pfField= this.activeTab.pfField;
    	var newmaster = record.data['currentmaster'];
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
    	    	var url=basePath+this.activeTab.url;
    	    	if(me.BaseUtil.contains(url, '?', true)){
    	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	} else {
    	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	}
    	    	myurl += "&datalistId=" + main.getActiveTab().id;

				if( newmaster ){
				//	   myurl += "&newMaster=" + newmaster;
					   var currentMaster = parent.window.sob;
					   if ( currentMaster && currentMaster != newmaster) {// 与当前账套不一致
						   me.openModalWin(newmaster, currentMaster, myurl);return;
					   }
				}

    	    	main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页    	
    	    	panel = {       
    	    			title : me.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
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
    openModalWin: function(master, current, url) {
 	   if (parent.Ext) {
 		   Ext.Ajax.request({
 			   url: basePath + 'common/changeMaster.action',
 			   params: {
 				   to: master
 			   },
 			   callback: function(opt, s, r) {
 				   if (s) {
 					   var localJson = new Ext.decode(r.responseText);
 					   var win = parent.Ext.create('Ext.Window', {
 						   width: '100%',
 						   height: '100%',
 						   draggable: false,
 						   closable: false,
 						   modal: true,
 						   id:'modalwindow',
 						   historyMaster:current,
 						   title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
 						   html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
 						   buttonAlign: 'center',
 						   buttons: [{
 							   text: $I18N.common.button.erpCloseButton,
 							   cls: 'x-btn-blue',
 							   id: 'close',
 							   handler: function(b) {
 								   Ext.Ajax.request({
 									   url: basePath + 'common/changeMaster.action',
 									   params: {
 										   to: current
 									   },
 									   callback: function(opt, s, r) {
 										   if (s) {
 											   b.up('window').close();
 										   } else {
 											   alert('切换到原账套失败!');
 										   }
 									   }
 								   });
 							   }
 						   }]
 					   });
 					   win.show();
 				   } else {
 					   alert('无法创建到账套' + master + '的临时会话!');
 				   }
 			   }
 		   });
 	   }
    },
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
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
    	var grid = this.activeTab;
    	var keyField=grid.keyField;
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
    },
    
});