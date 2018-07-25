Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeMain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.knowledge.KnowledgeMain','oa.knowledge.KnowledgeTreePanel','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Update','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    	    'erpKnowledgeTreePanel': {
    			itemmousedown: function(selModel, record){
    			    var tree=Ext.getCmp('KnowledgeTree');    			    
    				if(record.get('leaf')){	
    				Ext.getCmp('treeadd').setDisabled(true);
    				Ext.getCmp('treedelete').setDisabled(false);
    				Ext.getCmp('treeupdate').setDisabled(false);
    				tree.select=new Object();
    			    tree.select.id=record.get('id');
    			    tree.select.name=record.get('text');
    			    var id=record.get('id');
    				condition="kl_kindid="+id;
    				Ext.getCmp('pagingtoolbar').child('#inputItem').setValue(1);
    				page=1;
    				Ext.getCmp('grid').getCount(caller,condition);
					}
					else if(!record.get('leaf')){
					   Ext.getCmp('treeadd').setDisabled(false);
					   Ext.getCmp('treedelete').setDisabled(true);
					   Ext.getCmp('treeupdate').setDisabled(true);
					   tree.select=new Object();
    			       tree.select.id=record.get('id');
    			       tree.select.name=record.get('text');
					
					}
    			}
    		},
    	    'erpRecordGridPanel': {
    		  itemclick: this.onGridItemClick
    		 },
    		'button[id=treeadd]':{
    		  click:function(btn){
			  	var win = new Ext.window.Window({
			    	id : 'win',
   				    height: '300',
   				    width: '450',
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',
   				    	  layout : 'fit',
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeKind' 
   				    	  +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				         
    	     });
    	    win.show();	
           },
         },
         'button[id=treeupdate]':{
    		  click:function(btn){
    		  var id=Ext.getCmp('KnowledgeTree').select.id;
			  	var win = new Ext.window.Window({
			    	id : 'win',
   				    height: '300',
   				    width: '450',
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',
   				    	  layout : 'fit',
   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeKind&formCondition=kk_id='+id 
   				    	  +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   				    }],
   				         
    	     });
    	    win.show();	
           },
         },
         'button[id=treedelete]':{
            click:function(btn){
            var tree=Ext.getCmp('KnowledgeTree')
               var id=tree.select.id;
                Ext.Ajax.request({
			   		url : basePath + 'common/deleteCommon.action',
			   		params: {
			   		    caller:'KnowledgeKind',
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.success){
			   				delSuccess(function(){	
			   					tree.getTreeRootNode();						
							});//@i18n/i18n.js
			   			} else {
			   				delFailure();
			   			}
			   		}
				});
            }   
         },
    		'erpUpdateButton':{
    		   afterrender: function(btn){
    					btn.hide();   				
    			},   			
    		},	   	  	   		
    		'erpCloseButton': {
    		   afterrender: function(btn){    			 
				  var value=Ext.getCmp('wr_taskpercentdone').getValue();
    		      Ext.getCmp('wr_progress').updateProgress(value/100,'当前任务进度:'+Math.round(value)+'%');   			
    			  Ext.getCmp('wr_redcord').setHeight(350);			
    			},  
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUploadButton':{
    		   afterrender: function(btn){
    			btn.hide();  
    			},
    		},
    	
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
    	    			title : '知识('+title+')',
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
    	    					if(!main){
    	    						main = parent.parent.Ext.getCmp("content-panel");
    	    					}
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
    }, getCurrentStore: function(value){
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