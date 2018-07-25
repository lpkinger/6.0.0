Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeMap', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.knowledge.KnowledgeMap','oa.knowledge.KnowledgeTreePanel','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    	    'erpKnowledgeTreePanel': {
    			itemmousedown: function(selModel, record){
    				if(record.get('leaf')){	
    				var id=record.get('id');
    				condition="kl_kindid="+id;
    				Ext.getCmp('pagingtoolbar').child('#inputItem').setValue(1);
    				page=1;
    				Ext.getCmp('grid').getCount(caller,condition);
					}
					
    			}
    		},
    	    'erpDatalistGridPanel': { 
    		//	itemclick: this.onGridItemClick 
    		  },
    		'erpSaveButton': {
    		  afterrender: function(btn){
    			btn.hide();  
    			},
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
        var scanpersonid=record.data.kl_scanpersonid+'#';
        var authorid=record.data.kl_authorid;
    	if(scanpersonid&&scanpersonid.indexOf(emid)<0&&authorid!=emid){
    	  //说明没有权限查看  需提出申请
    	  var win = new Ext.window.Window(
				{
					id : 'win',
					height : '350',
					width : '550',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeApply'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
		return;
    	}
    	if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
    		var value = record.data[keyField];
        	var formCondition = keyField + "IS" + value ;
        	var gridCondition = pfField + "IS" + value;
        	var mappingCondition='kl_kindidIS'+record.data.kl_kindid+' And '+keyField+'NO'+value;
        	var panel = Ext.getCmp(caller + keyField + "=" + value); 
        	var main = parent.Ext.getCmp("content-panel");
        	if(!main){
				main = parent.parent.Ext.getCmp("content-panel");
			}
        	if(!panel){ 
        		var title = "";
    	    	if (value.toString().length>4) {
    	    		 title = value.toString().substring(value.toString().length-4);	
    	    	} else {
    	    		title = value;
    	    	}
    	    	var myurl = '';
    	    	if(me.BaseUtil.contains(url, '?', true)){
    	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition+'&mappingCondition='+mappingCondition;
    	    	} else {
    	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition+'&mappingCondition='+mappingCondition;
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
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
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