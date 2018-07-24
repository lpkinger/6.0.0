Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AddressBook', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
	views:[
	       'oa.attention.AddressBook','oa.attention.AttentionGrid','oa.attention.PersonalAddressTreePanel','oa.attention.Form','core.form.FileField','core.form.PhotoField',
	       'core.button.Save','core.button.Close','core.button.Update','oa.mail.TreePanel','oa.attention.PublicAddressGrid','oa.attention.AddressQueryForm','core.trigger.DbfindTrigger','oa.attention.EmployeeTreePanel'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    	       'hidden[id=file-hidden]':{
	    	       change:function(field){
	    	        console.log(field.value );
	    	       }
	    	       },	
	    	       'hidden[id=ab_recorderid]':{
	    	       afterrender:function(field){
	    	          if(field.value!=emid){
	    	           var btn=Ext.getCmp('updatebutton');
	    	           if(btn)  btn.setDisabled(true);
	    	             Ext.getCmp('publicqueryform').resize();//坑爹的样式
	    	          }
	    	       }
	    	       
	    	       },    	   
	    	       'erpMailTreePanel': {
    			afterrender: function(tree){
    				tree.selModel.on('select', function(selModel, record){
    					record.selected = true;
    					if(record.childNodes.length > 0){
    						selModel.isOnSelect = true;
    						selModel.select(record.childNodes);
    						Ext.each(record.childNodes, function(){
    							this.selected = true;
    						});
    						selModel.isOnSelect = false;
    						me.setSharedName(selModel.getSelection());
    						me.setSharedId(selModel.getSelection());   						
    					} else {
    						if(!selModel.isOnSelect){
    							var arr = selModel.getSelection();
        						arr.push(record);
        						selModel.isOnSelect = true;
        						selModel.select(arr);
        						selModel.isOnSelect = false;
        						me.setSharedName(selModel.getSelection());
    						   me.setSharedId(selModel.getSelection()); 
    						}
    						return;
    					}
    				});
    				tree.selModel.on('deselect', function(selModel, record){
    					record.selected = false;
    					if(record.childNodes.length > 0){
    						selModel.deselect(record.childNodes);
    						Ext.each(record.childNodes, function(){
    							this.selected = false;
    						});
    						me.setSharedName(selModel.getSelection());
    						   me.setSharedId(selModel.getSelection()); 
    					} else {
    						selModel.deselect(record);
    						me.setSharedName(selModel.getSelection());
    						me.setSharedId(selModel.getSelection()); 
    						return;
    					}
    				});
    			}
    		},
	    		   'button[id=attention]':{
	    			   click:function(){
	    			   }
	    		   },
	    		   'textfield[id=search]':{
	    		     change:function(){
	    		     me.onTextFieldChange();
	    		     }	    		   
	    		   },
	    		   'button[id=adduser]':{
	    		      click:function(){
	    		      me.addUser(me);
	    		      }
	    		   },
	    		   'button[id=deleteuser]':{
	    		     click:function(){
	    		      me.deleteUser(me);
	    		     }
	    		   },
	    		   'combo[id=moveto]':{
	    		    select:function(combo,records){
	    		       var groupid=records[0].data.id;
	    		       me.moveToOtherGroup(groupid,me);
	    		    }	     
	    		   },
	    		   'erpEmployeeTreePanel':{
	    		     show:function(panel){
	    		        if(panel.select==null){
	    				    panel.getSelectionModel().select(panel.getStore().tree.root.childNodes[0],true);
	    				   }
	    		     },
	    		      selectionchange:function(model,data, eOpts ){
	    		        var orgid=data[0].data.id;
	    		           
	    		        var grid=Ext.getCmp('employeeAddressBook');
	    		        var findcondition='em_defaultorid='+orgid.replace(/org/g,"");			      
	    		        var gridParam = {caller: grid.caller, condition:findcondition };
    	                     grid.loadNewStore(grid,gridParam);
	    		      } 
	    		   
	    		   },
	    		   'erpPersonalAddressTreePanel':{
	    			   afterrender:function(panel){
	    				   var item=new Object();
	    				   var button=new Object();
	    				   button.xtype='button';
	    				   button.cls='btn-cls';
	    				   button.text='点击添加分组';
	    				   button.iconCls='x-button-icon-addgroup';
	    				   button.style='margin-left:20px;';
	    				   button.handler=function open(){
	    					   me.addGroup();
	    				   };
	    				   panel.add(button);
	    				   if(panel.select==null){
	    				    panel.getSelectionModel().select(panel.getStore().tree.root.childNodes[0],true);
	    				    
	    				   }
	    			   },
	    			   selectionchange:function(model,data, eOpts ){
	    			   var groupid=data[0].data.id;	   			   
	    			     Ext.getCmp('groupkind').setValue(data[0].data.text.substring(0,data[0].data.text.indexOf('('))+" ("+data[0].data.qtitle+"...)");
	    			     Ext.getCmp('groupid').setValue(groupid);
	    			     var grid=Ext.getCmp('AttentionGridPanel');
	    			      var findcondition=(groupid==0) ?'ab_recorderid='+emid : 'ab_groupid='+groupid+'  AND ab_recorderid='+emid;
	    				  var gridParam = {caller: caller, condition:findcondition };
    	                     grid.loadNewStore(grid,gridParam);	    			   
	    			   },
	    			   itemmousedown:function(view,record,el,index, e,eOpts){
                           
	    				   var menu=Ext.getCmp('mainmenu');
	    				   if(menu){
	    					   menu.close();
	    				   }

	    			   },
	    			   containerclick:function(){
	    				   Ext.getCmp('mainmenu').close();

	    			   },
	    			   itemcontextmenu:function(view,record,el,index, e,eOpts) {
	    				   view.getSelectionModel().select(index);
	    				   var id=record.data.id;
	    				   var bool=id<1;
	    				   var menu=Ext.create('Ext.menu.Menu', {
	    					   style: {
	    						   overflow: 'visible'    
	    					   },
	    					   ownerCt : this.ownerCt,
	    					   renderTo:Ext.getBody(),
	    					   floating: false,
	    					   id:'mainmenu',
	    					   async:false,
	    					   width: 100,	
	    					   autoHeight:true,
	    					   plain: true,
	    					   items: [{
	    					        text:'共 享',
	    					        iconCls:'x-menu-share',
	    					         listeners:{
	    							   click:function(){
	    							        menu.close();
	    							        me.share('group'); 
	    							   }
	    						   }
	    					        
	    					   },{
	    						   text: '添加分组',
	    						   iconCls:'x-button-icon-addgroup',
	    						   listeners:{
	    							   click:function(){
	    							        menu.close(); 
	    								   me.addGroup(view,index);
	    								   
	    							   }
	    						   }
	    					   },{
	    						   text: '添加联系人',
	    						   iconCls:'x-menu-adduser',
	    						   disabled:bool,
	    						   listeners:{
	    							   click:function(){
	    							       menu.close(); 
	    								   me.addUser(me);
	    							   }
	    						   }
	    					   },'-',{
	    						   text: '重命名',
	    						   iconCls:'x-menu-rename',
	    						   disabled:bool,
	    						   listeners:{
	    							   click:function(){
	    							       menu.close(); 
	    								   me.renameGroup(id);
	    							   }
	    						   }
	    					   },{
	    						   text:'删除改组',
	    						   iconCls:'x-button-icon-deletedetail',
	    						   disabled:bool,
	    						   listeners:{
	    							   click:function(){
	    							       menu.close();
                                           me.deleteGroup(id,'oa/addressbook/deleteAddressBookGroup.action');
                                           
	    							   }
	    						   }
	    					   }]
	    				   });
	    				   menu.showAt(e.getXY());
	    			   },	  
	    		   }	
	    	   });
	       },
	       addGroup:function(view,index){
	    	   var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '300',
	    		   width: '500',
	    		   title:'添加新的分组',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 100%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:'AddressBookGroup'	, 
	    			   saveUrl:'oa/addressbook/saveAddressBookGroup.action', 				       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',
	    				   handler:function(){
	    				       var name=Ext.getCmp('ag_name').getValue();
	    				       var tree= Ext.getCmp('PersonalAddressTree');
	    				       var closebool=true;
	    				       Ext.Array.each(tree.getStore().tree.root.childNodes,function(node){
	    				        if(node.data.qtitle==name){
	    				        closebool=false;
	    				          	Ext.Msg.alert($I18N.common.msg.title_prompt, '组名已存在!',function(){
	    				          	Ext.getCmp('ag_name').reset();
	    				          	});
	    				          	return;
	    				        }	    				        
	    				       });
	    				       if(closebool){
	    					   Ext.getCmp('form').save();
	    					   Ext.getCmp('win').close();
	    					   tree.getTreeRootNode(tree);
	    					   }
	    					   var groupid=Ext.getCmp('groupid').getValue();
	    					  tree.getSelectionModel().select(tree.getStore().getNodeById(groupid),true)
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }],

	    	   });
	    	   win.show();	   
	       },
	       renameGroup:function(id){ 
	    	   var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '300',
	    		   width: '500',
	    		   title:'分组重命名',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 100%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:'AddressGrade'	, 
	    			   formCondition:'ag_id='+id,
	    			   updateUrl:'oa/addressbook/updateAddressBookGroup.action', 				       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',
	    				   handler:function(){
	    				       var name=Ext.getCmp('ag_name').getValue();
	    				       var tree= Ext.getCmp('PersonalAddressTree');
	    				       var closebool=true;
	    				        if(Ext.getCmp('ag_name').originalValue==name){
	    				          closebool=false;
	    				          	Ext.Msg.alert($I18N.common.msg.title_prompt, '未作修改!',function(){
	    				          	Ext.getCmp('ag_name').reset();
	    				          	});
	    				          	return;
	    				        }	    				        
	    				       if(closebool){
	    					   Ext.getCmp('form').update();
	    					   Ext.getCmp('win').close();
	    					   tree.getTreeRootNode(tree);
	    					    tree.getSelectionModel().select(tree.getStore().getNodeById(id),true);
	    					   }
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }],

	    	   });
	    	   win.show();	   
	       },
	   deleteGroup:function(id,deleteUrl){
	      var me = this;
		warnMsg('确定要删除该分组吗?', function(btn){
			if(btn == 'yes'){	
				Ext.Ajax.request({
			   		url : basePath + deleteUrl,
			   		async: false,
			   		params: {
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){			   		
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}else {
		        		   var tree= Ext.getCmp('PersonalAddressTree');
	    					   tree.getTreeRootNode(tree);
	    					   //删除完成之后选中第一个
	    					   tree.getSelectionModel().select(tree.getStore().tree.root.childNodes[0],true);
		        		}	
			   		}
				});
			}
		});
	   } ,
	   addUser: function(me){
	     var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '80%',
	    		   width: '60%',
	    		   title:'添加联系人',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   anchor : '100% 100%',	
	    			   xtype:'erpAttentionFormPanel',
	    			   caller:'AddressBook'	, 
	    			   saveUrl:'/oa/addressbook/saveAddressPerson.action', 				       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',
	    				   handler:function(){
	    				   var groupid=Ext.getCmp('groupid').getValue();
	    				       Ext.getCmp('ab_groupid').setValue(groupid);
	    					   Ext.getCmp('form').save();
	    					   Ext.getCmp('win').close();
	    					  me.reLoadTreeAndGrid();
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }],

	    	   });
	    	   win.show();
	   },
	   deleteUser:function(me){
	      //批删除 
	      var grid=Ext.getCmp('AttentionGridPanel')
	       var params=grid.getMultiSelected();
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/addressbook/deleteAddressPerson.action',
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
			   				Ext.Msg.alert("提示", "删除成功!", function(){
			   					grid.multiselected = new Array();
			   				  me.reLoadTreeAndGrid();
			   				});
			   			}
			   		}
		   });
	   },
	   moveToOtherGroup:function(groupid,me){
	   var grid=Ext.getCmp('AttentionGridPanel');
	    var params=grid.getMultiSelected();
	          params.id=groupid;
	     		var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/addressbook/removeToOtherGroup.action',
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
			   					me.reLoadTreeAndGrid();
			   					Ext.getCmp('deleteuser').setDisabled(true);
			   				});
			   			}
			   		}
		   });
	   } ,
	   share:function(type){
	      var win = new Ext.window.Window({
	    		   id : 'win',
	    		   height: '100%',
	    		   width: '60%',
	    		   title:'共享联系人',
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'fit',
	    		   items: [{ 
				    id:'desk', 
				    layout: 'border', 
				    items: [{
	    			   tag : 'iframe',
	    			   frame : true,
	    			   region: 'center',
	    			   width: '50%',	
	    			   xtype:'form',
	    			   id:'form',
	    			   layout:'column',
	    			   items:[{
	    			   fieldLabel:'<img src="' + basePath + 'resource/images/icon/share.png" style="width:30%">共享给',
	    			   id:'sharedname',
	    			   name:'sharedname',
	    			   columnWidth:1,
	    			   allowBlank:false,
	    			   fieldStyle:'background:#f0f0f0;color:#515151;',
	    			   xtype:'textarea',
	    			    rows: 6,	    			   
	    			   },{
	    			    id:'sharedid',
	    			     name:'sharedid',
	    			    xtype:'textfield',
	    			    hidden:'true',   
	    			   }],			       
	    			   bbar:['->',{
	    				   xtype:'erpSaveButton',
	    				   handler:function(){
		                 var params = new Object();
			var r = Ext.getCmp('form').getValues();
			params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
			
			 if(type=="group"){
			  var groupid=Ext.getCmp('groupid').getValue();
			 var data = new Array();
			 var object=new Object();
			  object.ab_groupid=groupid;
			  data.push(object);			
			 params.data= Ext.encode(data);
			 }
		     params.type = type;
		     Ext.Ajax.request({
	   		 url : basePath + 'oa/addressbook/sharedToOther.action',
	   		 params : params,
	   		 method : 'post',
	   		 async: false,
	   		 callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			 if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   					showError(str);
	   				} 
	   		    }
		       });
  					   Ext.getCmp('win').close();	    					
	    				   }
	    			   },{
	    				   xtype:'erpCloseButton',
	    				   handler:function(){
	    					   Ext.getCmp('win').close();
	    				   } 
	    			   },'->']         
	    		   }, {
					  region: 'east',
					  width: '50%',
					  xtype: 'erpMailTreePanel',
				    }]
			      }]   

	    	   });
	    	   win.show();
	   },
	   setSharedName: function(records){
		var r = '';
		Ext.each(records, function(){
			if(r != ''){
				r += ';';
			}
			r += this.get('text');
		});
		Ext.getCmp('sharedname').setValue(r);
	},
	setSharedId: function(records){
		var r = '';
		Ext.each(records, function(){
			if(r != ''){
				r += ';';
			}
			r += Math.abs(this.get('id'));
		});
		Ext.getCmp('sharedid').setValue(r);
	},
	   reLoadTreeAndGrid:function(){
	     var tree= Ext.getCmp('PersonalAddressTree');
		var groupid=Ext.getCmp('groupid').getValue();
		var findcondition=(groupid==0) ?'ab_recorderid='+emid : 'ab_groupid='+groupid+'  AND ab_recorderid='+emid;
	    var gridParam = {caller: caller, condition:findcondition };
	    var grid=Ext.getCmp('AttentionGridPanel');
    	grid.loadNewStore(grid, gridParam);    	                       
         tree.getTreeRootNode(tree);
        tree.getSelectionModel().select(tree.getStore().getNodeById(groupid),true);
	   },
	     onTextFieldChange: function() {
         var me = Ext.getCmp(gridid);
         me.view.refresh();
         me.searchValue = me.getSearchValue();
         me.indexes = [];
         me.currentIndex = null;
         if (me.searchValue !== null) {
             me.searchRegExp = new RegExp(me.searchValue, 'g' + (me.caseSensitive ? '' : 'i')); 
             me.store.each(function(record, idx) {
                 var td = Ext.fly(me.view.getNode(idx)).down('td'),
                     cell, matches, cellHTML;
                 while(td) {
                     cell = td.down('.x-grid-cell-inner');
                     matches = cell.dom.innerHTML.match(me.tagsRe);
                     cellHTML = cell.dom.innerHTML.replace(me.tagsRe, me.tagsProtect);
                     
                     // populate indexes array, set currentIndex, and replace wrap matched string in a span
                     cellHTML = cellHTML.replace(me.searchRegExp, function(m) {
                        if (Ext.Array.indexOf(me.indexes, idx) === -1) {
                            me.indexes.push(idx);
                        }
                        if (me.currentIndex === null) {
                            me.currentIndex = idx;
                        }
                           return '<span class=" x-livesearch-matchbase">' + m + '</span>';
                     });
                     
                     // restore protected tags
                     Ext.each(matches, function(match) {
                        cellHTML = cellHTML.replace(me.tagsProtect, match); 
                     });
                     // update cell html
                     cell.dom.innerHTML = cellHTML;
                     td = td.next();
                 }
             }, me);
             if (me.currentIndex !== null) {
                 me.getSelectionModel().select(me.currentIndex);
             }
         }

         // no results found
         if (me.currentIndex === null) {
             me.getSelectionModel().deselectAll();
         }
        Ext.getCmp('search').focus();
     },
     
});