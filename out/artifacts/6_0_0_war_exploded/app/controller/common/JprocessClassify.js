Ext.QuickTips.init();
Ext.define('erp.controller.common.JprocessClassify', {
    extend: 'Ext.app.Controller',
    views:[
    		'common.JProcessDeploy.JprocessClassify','core.grid.Panel2','core.grid.Panel2','core.grid.YnColumn','core.button.DeleteDetail'
    	],
    GridUtil: Ext.create('erp.util.GridUtil'),
    init:function(){
    	var me = this;
    	this.control({
    		'erpExportDetailButton':{
    			afterrender:function(btn){
    				btn.ownerCt.add({
    	                  xtype:'combo',
    	                  fieldLabel:'移动到',
    	                  disabled:true,
    	                  labelSeparator :'',
    	                  labelAlign:'right',
    	                  style:'margin-left:10px;margin-top:10px',
    	                  fieldStyle:"background:#FFFAFA;color:#515151; height:20px",
    	                  labelStyle:'font-size:14px',
    	                  id:'moveto',
    	                  queryMode :'local',
    			          displayField:'display',
    				      valueField :'value',
    				      listeners:{
    				    	select :function(combo){
    				    		var grid=Ext.getCmp('grid');
    				    	    var params=grid.getMultiSelected();
    				    	          params.id=combo.value;    	
    				    	     		var main = parent.Ext.getCmp("content-panel");
    				    				main.getActiveTab().setLoading(true);//loading...
    				    				Ext.Ajax.request({
    				    			   		url : basePath + 'common/removeToOtherClassify.action',
    				    			   		params: params,
    				    			   		method : 'post',
    				    			   		callback : function(options,success,response){
    				    			   			main.getActiveTab().setLoading(false);
    				    			   			var localJson = new Ext.decode(response.responseText);
    				    			   			if(localJson.exceptionInfo){
    				    			   				showError(localJson.exceptionInfo);
    				    			   				return "";
    				    			   			}
    				    		    			if(localJson.success){
    				    		    				if(localJson.log){
    				    		    					showMessage("提示", localJson.log);
    				    		    				}
    				    		    				Ext.getCmp('moveto').reset();
    				    		    				Ext.getCmp('moveto').setDisabled(true);
    				    			   				Ext.Msg.alert("提示", "移动成功!", function(){
    				    			   					grid.multiselected = new Array();
    				                                    me.GridUtil.loadNewStore(grid,{caller:caller,condition:'1=1'});
    				    			   				});
    				    			   			}
    				    			   		}
    				    		   });
    				    		
    				    		
    				    	}  
    				      },
    				      onTriggerClick:function(){
    				    	  var me = this;
    				    	  var data=new Array();
    				    	  var o=null;
    				    	  Ext.Ajax.request({
    				    		  url : basePath + 'common/getAllJProClassify.action',
    				    		  async: false,
    				    		  method : 'post',
    				    		  callback : function(options,success,response){			   		
    				    			  var res = new Ext.decode(response.responseText);
    				    			  if(res.exceptionInfo){
    				    				  showError(res.exceptionInfo);return;
    				    			  }else {
    				    				  Ext.Array.each(res.data, function(tr) {
    				    					  if(tr){
    				    						  o=new Object();
    				    						  o.display=tr[1];
    				    						  o.value=tr[0];
    				    						  data.push(o);
    				    					  }
    				    				  });
    				    			  }	
    				    		  }
    				    	  });
    				    	  console.log(data);
    				    	  this.getStore().loadData(data);
    				    	  Ext.create('Ext.data.Store', {
    				    		  fields: ['display', 'value'],
    				    		  data :data
    				    	  });
    				    	  if (!me.readOnly && !me.disabled) {
    				    		  if (me.isExpanded) {
    				    			  me.collapse();
    				    		  } else {
    				    			  me.expand();
    				    		  }
    				    		  me.inputEl.focus();
    				    	  }    
    				      }      
    	                 });
    			}
    		 }
    	});
    },
    loadNode: function(selModel, record){
    	var me = this;
    	if (!record.get('leaf')) { 
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
				me.flag = true;
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					var activeTab = me.getActiveTab();
					activeTab.setLoading(true);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'common/getLazyJProcessDeploy.action',
			        	params: {
			        		parentId: record.data['id']
			        	},
			        	callback : function(options,success,response){
			        		activeTab.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			var tree = res.tree;
			        			Ext.each(tree, function(t){
			        				t.jd_selfId = t.id;
			        				t.jd_parentId = t.parentId;
			        				t.jd_classifiedName = t.text;
			        			//	t.sn_isleaf = t.leaf;
			        				t.jd_caller = t.creator;
			        				t.jd_formUrl = t.url;
			        				t.jd_processDefinitionId = t.qtitle;
			        				t.jd_enabled = t.using;
			        				t.jd_processDefinitionName = t.version;
			        				t.dirty = false;
			        			});
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        			Ext.each(record.childNodes, function(){
			        				this.dirty = false;
			        			});
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	}
    },
    getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	}
});