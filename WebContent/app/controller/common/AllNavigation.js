Ext.QuickTips.init();
Ext.define('erp.controller.common.AllNavigation', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    stores: ['TreeStore'], //声明该控制层要用到的store
    views: ['common.AllNavigation','common.main.NavigationTreePanel','core.trigger.DbfindTrigger'], //声明该控制层要用到的view
    init: function() {
        var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        this.control({
            'erpNavigationTreePanel': {//全功能导航
            	beforeitemdblclick: function(tree,record,item,index,e) {
            		return false;
            	},
            	cellclick:function(tree,td,cellIndex,record,tr,rowIndex,e,eOpts ){
            		if(cellIndex==0){
            			if(e.target.getAttribute('class').indexOf("x-tree-elbow-img") == -1){//不是点击 + —
            				if(record.raw.updateflag==1){
	            				me.updeteSyanavigation(tree, rowIndex, cellIndex,td,e);
	            			}else{
	            				me.openSyanavigationDetail(tree, rowIndex, cellIndex, td, e);
	            			}
            			}            			
            		}else if(cellIndex==1){//升级
            			if(record.raw.updateflag==1){
            				me.updeteSyanavigation(tree, rowIndex, cellIndex,td,e);
            			}
            		}else if(cellIndex==2){//查看说明
            			me.openSyanavigationDetail(tree, rowIndex, cellIndex, td, e);
            		}
            	}
            },
            '#navigationtree': {//点击添加到的导航树
	    		itemmousedown: function(selModel, record){
		    		var tree = selModel.ownerCt;
		    		me.loadTree(tree, record);
		    	  }
	    	}
        });
    },
    //升级
    updeteSyanavigation:function(view, rowIndex, colIndex, column, e){
    	var me=this,addToPath='';
    	var p=Ext.getCmp('descPanel');
    	panel=p.ownerCt;
    	var record = view.getRecord(view.findTargetByEvent(e)),
		    	id=record.get('id'),num_ = record.raw.num;
		var UpdateInfoPanel=Ext.getCmp('UpdateInfo'+id);
		if (UpdateInfoPanel) {
			return; 
		}
    	panel.setLoading(true);
    	Ext.Ajax.request({
	      url : basePath + 'common/getUpdatePath.action',
	      params: {
	      	id:id,
	      	num:num_
	      },
	      callback : function(options,success,response){
	        panel.setLoading(false);
	    	var res = new Ext.decode(response.responseText);
	    	if(res.success){
	    		if(res.addTo){
	    			addToPath=res.path;//UAS默认路径
	    		}
	    		uasPath=res.uaspath;
	    		desc_=res.desc;//升级描述
	    		needdeploy=res.needdeploy;//需要发布
	    		addToReadOnly=res.addTo;//默认添加到的路径在客户导航中是否存在
	    		p.removeAll();
				p.add({
					xtype:'panel',
					border:false,
					id:'UpdateInfo'+id,
					layout: {
				        type: 'vbox',
				        align: 'left'
				    },
		    		defaults: {	margin : '0 1 5 1'},
					items:[{xtype:'panel',width:'100%',border:false,layout:'column',
							items:[{xtype:'dbfindtrigger', fieldLabel: '添加到',columnWidth: 0.6,labelWidth:45,height:22,width:'50%',margin : '5 1 10 2',
						   			id:'addTo',name:'addTo', autoDbfind : false,value:addToPath,editable:false,labelStyle: 'color:red',
								   		onTriggerClick : function(){me.getModuleTree();}
					    		   	},{xtype:'tbtext',height:22,margin : '0 0 0 2',text:'默认路径:'+uasPath,readOnly:true,columnWidth: 0.5						
									},{xtype:'hidden',id:'addToId',value:-1
					    		   	},{xtype : 'tbtext',text:'<font color=red>*新增此功能需要暂停服务器进行程序发布，请联系我们的售后400-830-1818</font>'
									,height:25,hidden:!needdeploy,columnWidth: 1}
									],
									buttonAlign : 'center',
							    	buttons:[{text:'确定',iconCls: 'x-button-icon-save',cls: 'confirmButton',
										handler: function() {
											if(em_type && em_type!='admin'){
									    		showError('ERR_POWER_025:您没有升级的权限,请联系管理员!');return;
									    	}
											var addTo=Ext.getCmp('addTo').value;
											var addToId=Ext.getCmp('addToId').value;
											if(addTo==''){
												showError('请选择要添加到的位置！');
												return;
											}
											p.setLoading(true);
											Ext.Ajax.request({
										      url : basePath + 'common/updateNavigation.action?_noc=1',
										      params: {
										    	id: id,
										    	addToId:addToId
										      },
										      callback : function(options,success,response){
										      	 p.setLoading(false);
										      	 var win = parent.Ext.getCmp("allNavigationWindow");
										      	 win.upgrade=true;
										      	 Ext.MessageBox.alert("提示","升级成功",function(btn){  
										      	 	Ext.getCmp('navigation-panel').getTreeRootNode(0);
										            p.removeAll();
										        	p.add({xtype:'panel',bodyStyle:'background:#E5E5E5;',
															html:'<div align="center" class="default-panel"><img src="'+basePath+'resource/images/upgrade_default.png"></div>' });
										        },this);  
										      }
										     });
									}
								}]
							},{xtype:'panel',title:'功能说明:' ,width:'100%',flex: 1,layout:'anchor',
								items:[{xtype:'htmleditor',hideLabel:true,readOnly:true,value:desc_,height:400, 
										fieldStyle :'background:white; border: 1px solid #F6F6F6;',anchor:"100% 100%",
										enableColors: false,enableAlignments: false,enableLists: false,enableSourceEdit: false,  enableLinks: false,
										enableFont: false,enableFormat: false,enableFontSize: false,
										listeners:{
											afterrender: function(editor) {
												//editor.getToolbar().hide();
												Ext.Ajax.request({
											      url : basePath + 'common/getUpdateInfo.action?_noc=1',
											      timeout:30000,
											      params: {
											    	num:num_
											      },
											      callback : function(options,success,response){
											      		var res = new Ext.decode(response.responseText);
	    												if(res.success){
	    													editor.setValue(res.desc);
	    												}
											      }
											     });
											}
										}
							}]
					}]					
				});
	    	} else if(res.exceptionInfo){
		    	showError(res.exceptionInfo);
		    }
		  }
	   });
    },
    getModuleTree:function(){//选择添加到路径
     var w = Ext.create('Ext.Window',{
	    	 title: '添加到',
	    	 height: "80%",
	    	 width: "50%",
	    	 maximizable : false,
	    	 modal: true,
	    	 buttonAlign : 'center',
	    	 layout : 'anchor',
	    	 items: [{
	    		 anchor: '100% 100%',
	    		 xtype: 'treepanel',
	    		 id:'navigationtree',
	    		 rootVisible: false,
	    		 cls:'addToTree',
	    		 num:'',
	    		 margin : '0 0 0 -10', 
	    		 useArrows: true,
	    		 store: Ext.create('Ext.data.TreeStore', {
	    			 storeId:'navigationTreeStore',
	    			 root : {
	    				 text: 'root',
	    				 id: 'root',
	    				 expanded: true
	    			 }
	    		 })
	    	 }],
	    	 buttons : [{
	    		 text: '确定',
	    		 iconCls: 'x-button-icon-confirm',
	    		 cls: 'x-btn-gray',
	    		 handler: function(btn){
	    		    var t = btn.ownerCt.ownerCt.down('treepanel');
	    			if(!Ext.isEmpty(t.title)) {
	    				Ext.getCmp('addTo').setValue(t.title);
	    				//Ext.getCmp('addToNum').setValue(t.num);
	    				Ext.getCmp('addToId').setValue(t.addtoid);
	    			}
	    			btn.ownerCt.ownerCt.close();
	    		 }
	    	 },{
	    		 text : '关  闭',
	    		 iconCls: 'x-button-icon-close',
	    		 cls: 'x-btn-gray',
	    		 handler : function(btn){
	    			btn.ownerCt.ownerCt.close();
	    		 }
	    	}]
	    });
	    w.show();
	    this.loadTree(Ext.getCmp('navigationtree'), null);
	},
    loadTree: function(tree, record){
	    var pid = 0;
	    if(record) {
	    	if (record.get('leaf')) {
	    		 return;
	    	} else {
	    		if(record.isExpanded() && record.childNodes.length > 0){
	    			record.collapse(true, true);//收拢
	    			return;
	    		} else {
	    			if(record.childNodes.length != 0){
	    				record.expand(false, true);//展开
	    			    return;
	    			}
	    		}
	    	}
	    	pid = record.get('id');
	    }
	    tree.setLoading(true);
	    Ext.Ajax.request({
	      url : basePath + 'common/lazyTree.action?_noc=1',
	      params: {
	    	parentId: pid,
	    	condition: "sn_using=1 and sn_isleaf='F'"
	      },
	      callback : function(options,success,response){
	        tree.setLoading(false);
	    	var res = new Ext.decode(response.responseText);
	    	if(res.tree){
	    		if(record) {
	    			if(res.tree.length>0){
	    				record.appendChild(res.tree);
	    			}	    			
	    			record.expand(false,true);//展开
	    			tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
	    			tree.num=record.raw.num;
	    			tree.addtoid=record.raw.id;
	    		} else {
	    			tree.store.setRootNode({
		    		    text: 'root',
		    			id: 'root',
		    			expanded: true,
		    			children: res.tree
		    		});
		    	}
		    } else if(res.exceptionInfo){
		    	showError(res.exceptionInfo);
		    }
		  }
	   });
	},
    openSyanavigationDetail: function(view, rowIndex, colIndex, column, e){//全功能导航
    	var record = view.getRecord(view.findTargetByEvent(e)),
		    	id=record.get('id'),title = record.get('text');
		    	var p=Ext.getCmp('descPanel');
		    	p.removeAll();
		    	p.add({
					tag : 'iframe',
					style:{
						background:'#f0f0f0',
						border:'none'
					},						  
					frame : true,
					border : false,
					layout : 'fit',
					html : '<iframe src="' + basePath + 'jsps/common/navigationDetails.jsp?id='+id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	    		});
    },
    parseUrl: function(url) {
        var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
        if (id == null) {
            id = url.substring(0, url.lastIndexOf('.'));
        }
        if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
            url = url.replace(/session:em_uu/g, em_uu);
        }
        if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
            url = url.replace(/session:em_code/g, "'" + em_code + "'");
        }
        if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
            url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
        }
        if (contains(url, 'session:em_name', true)) {
            url = url.replace(/session:em_name/g, "'" + em_name + "'");
        }
        if (contains(url, 'session:em_type', true)) {
            url = url.replace(/session:em_type/g, "'" + em_type + "'");
        }
        if (contains(url, 'session:em_id', true)) {
            url = url.replace(/session:em_id/g,em_id);
        }
        if (contains(url, 'session:em_depart', true)) {
            url = url.replace(/session:em_depart/g,em_id);
        }
        return url;
    }
});