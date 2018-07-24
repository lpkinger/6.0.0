/**
 * UAS标准后台设置主页面
 * */
Ext.QuickTips.init();
Ext.define('erp.controller.sysmng.Default', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil','erp.util.GridUtil'],    
    views: ['sysmng.MainNavigation','sysmng.ProgressBar','sysmng.MainnavPanel',
    		'sysmng.basicset.BasicSetPanel',
    		'sysmng.basicset.dictionary.DictionaryPanel','sysmng.basicset.fixed.FixedPanel',
    		'sysmng.basicset.dictionary.DictionnaryDatalist','sysmng.basicset.dictionary.ToolBar',
    		'sysmng.basicset.fixed.FreezeFormPanel','sysmng.basicset.fixed.FreezeGridPanel1',
    		'sysmng.basicset.fixed.FreezeGridPanel2','sysmng.basicset.BasicSetBar',
    		'sysmng.basicset.BasicnavPanel'],
    init: function() {
    	var me = this;
 	   	this.BaseUtil = Ext.create('erp.util.BaseUtil');
	   	this.FormUtil = Ext.create('erp.util.FormUtil');
	   	this.GridUtil = Ext.create('erp.util.GridUtil');	
    	this.control({
	    	'erpdictionnarydatalist': { 
	    		 itemclick:this.onGridItemClick			   				   
			 },
	        '#VersionTreePanel': { 
	    		spcexpandclick : me.handleSpExpandClick,
				addclick : me.handleAddClick    			
	    	}, 		   
		  	'upgradSqlList' : {
				itemclick : function(selModel, record) {
					var formCondition = 'NUM_IS' + record.data['NUM_'];
					this.onAdd('upgradsql' + record.data['NUM_'],'升级SQL(' + record.data['VERSION_'] + ')',
					'jsps/sysmng/upgradesql.jsp?formCondition='+ formCondition, 'upgradsqlpanel');
				},
				beforeactivate : function(grid) {
					grid.getCount();
				}
			},
	    	'upgradsqlToolBar #Add':{
	    		click:function(){
	           		this.onAdd("addUpgradeSql", "升级SQL", 'jsps/sysmng/upgradesql.jsp','upgradsqlpanel');
	    		}	
	    	},
			'#mainnavpanel':{				
					changeCard:function(panel,direction,index){
						this.changeCard(panel,direction,index);
					}				
			}
    	});
    },    
    handleAddClick: function(view, rowIndex, colIndex, column, e) {
    	var me=this;
        var record = view.getRecord(view.findTargetByEvent(e)); 
        var sn_num=record.data.sn_num;
        var sn_svnversion=record.data.sn_svnversion;
        var sn_id=record.data.sn_id;
        me.createWindows(record,sn_num,sn_svnversion,sn_id);             
    },
    createWindows:function(record,sn_num,sn_svnversion,sn_id){
     	var addpanel = Ext.getCmp('VersionAddPanel');
     	var windows= Ext.create('widget.window', {
     		modal:true,
     		
     		resizable: false,
     		layout:'anchor',
			items:[{
	 			xtype: 'VersionAddPanel',
	 			anchor: '100% 100%',
	 			recorddata:record,
	 			listeners:{
	 				beforerender:function(){

	 					if(sn_num==""){
	 						Ext.getCmp('id').setValue(sn_id);
	 						Ext.getCmp('numid').allowBlank=false;
							Ext.getCmp('version').minValue=sn_svnversion+1;
	 						//Ext.getCmp('version').setReadOnly(true);
	 						//Ext.getCmp('remark').setReadOnly(true);	 					
	 					}else		 					
	 					{		 						
	 						Ext.getCmp('version').minValue=sn_svnversion+1;
	 						Ext.getCmp('id').setValue(sn_id);
	 						Ext.getCmp('numid').setValue(sn_num);
	 						//Ext.getCmp('numid').setReadOnly(true);
	 						//Ext.getCmp('version').allowBlank=false;
	 						
	 						//Ext.getCmp('remark').allowBlank=false;
	 						
	 					}	 					
	 				}	 			
	 			}	 			
			}]
		 }).show();
     },
   	 handleSpExpandClick:function(record){//新定义的
		var me=this;
		var treegrid = Ext.getCmp('VersionTreePanel');
		var selModel=treegrid.getSelectionModel();
		me.loadNode(selModel,record);
		treegrid.selModel.select(record);
		return false;
	},
 	loadNode: function(selModel,record){
		var me = this;
		if ( record.data['sn_id']) { 
			
			if(record.isExpanded() && record.childNodes.length > 0){
				me.flag = true;
				record.collapse(true,false);
			} else {	
				if(record.childNodes.length == 0){
					Ext.Ajax.request({//拿到tree数据
						url : basePath + 'upgrade/lazyTree.action',
			        	params: {
			        		parentId: record.data['sn_id']
			        	},
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.tree && res.tree.length>0){
								var tree = res.tree;
								Ext.each(tree, function(t){
									t.sn_id = t.id;
							        t.sn_parentid = t.parentId;
							        t.sn_displayname = t.text;
							        t.sn_detno = t.detno;
							        t.sn_isleaf = t.leaf;
							        t.sn_using = t.using;
							        t.sn_tabtitle = t.text;
							        t.sn_url = t.url;
							        t.dirty = false;
							        t.sn_deleteable = t.deleteable;
							        t.sn_showmode = t.showMode;
							        t.sn_logic = t.data.sn_logic;
							        t.sn_limit = t.data.sn_limit;
							        t.sn_caller = t.data.sn_caller;	
							        t.sn_addurl = t.data.sn_addurl;	
							        t.sn_show=t.data.sn_show,
				    				t.sn_standardDesc=t.data.sn_standardDesc; 
				    				
				    				t.sn_num=t.data.sn_num;
				    				t.sn_svnversion=t.data.sn_svnversion;
							        t.data = null;
								});
								me.flag=true;
								record.appendChild(tree);
								record.expand(false, true);//展开
								Ext.each(record.childNodes, function(){
									this.dirty = false;
								});
							} else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});
				} else {
					me.flag=true;			
				}
			}
		}
	},
  	openTab : function (panel,id,mainId){ 
		var o = (typeof panel == "string" ? panel : id || panel.id); 
		var main = this.getMain(mainId); 
		var tab = main.getComponent(o); 
		if (tab) { 
			main.setActiveTab(tab); 
		} else if(typeof panel!="string"){ 
			panel.id = o; 
			var p = main.add(panel); 
			main.setActiveTab(p); 
		} 
    },
   	onAdd: function(panelId, title, url,mainId){
		var main = this.getMain(mainId);
		if(main){
			panelId = panelId == null
			? Math.random() : panelId;
			var panel = Ext.getCmp(panelId); 
			if(!panel){ 
				var value = "";
				if (title.toString().length>10) {
					value = title.toString().substring(0,10);	
				} else {
					value = title;
				}
				if(!contains(url, 'http://', true) && !contains(url, basePath, true)){
					url = basePath + url;
				}
				panel = { 
						title : value,
						tag : 'iframe',
						tabConfig:{tooltip:title},
						border : false,
						layout : 'fit',
						iconCls : 'x-tree-icon-tab-tab',
						html : '<iframe id="iframe_add_'+panelId+'" src="' + url+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
						closable : true
				};
				this.openTab(panel, panelId,mainId);
			} else { 
				main.setActiveTab(panel);     				
			}
		} else {
			if(!contains(url, basePath, true)){
				url = basePath + url;
			}
			window.open(url);
		}
	},
	getMain: function(mainId){
		var main = Ext.getCmp(mainId);
		return main;
	},    	
 	onGridItemClick: function(selModel, record){//grid行选择
	 	var formCondition = 'object_nameIS' + record.data['OBJECT_NAME'];
	 	var gridCondition = formCondition;
	 	var isbasic=1;					// 增加一个参数来判断区分客户和管理员
	 	this.onAdd('datadictionary'+formCondition,record.data['OBJECT_NAME'],'jsps/ma/dataDictionary.jsp?isbasic='+isbasic+'&formCondition='+formCondition+'&gridCondition='+gridCondition,'dictionarypanel');		
   	}, 	       
	checkPower: function(moduleCode){
		var me = this;
		var resFlag = false;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url:basePath + 'sysmng/checkmodulepower.action',
			method:'get',
			async:false,
			params:{
				emCode:em_code,
				moduleCode:moduleCode
			},
			callback:function(options,success,response){
				if(success){
					var res = Ext.decode(response.responseText);
					if(res.power){
						resFlag = true;
					}
					me.FormUtil.setLoading(false);
				}
			}
		});
		return resFlag;
	},
	changeCard:function(panel,direction,index){
		var layout = panel.getLayout();	
		var a=index;
		var app = this.application;
		layout.setActiveItem(index);
		this.ActiveIndex_=index;
	    activeItem=layout.getActiveItem();		
		if(activeItem.type){
			var contrlPath=this.getContrlPath(activeItem.type);
			Ext.require("erp.controller."+contrlPath,function(){
				var Controller = app.getController(contrlPath);
				Controller.init();
			},this);
		}
		//Ext.getCmp('mainnavpanel').setTitle(activeItem.desc);
	},
	getContrlPath:function(type){
		return "sysmng.step."+type+"Controller";
	},
});