Ext.QuickTips.init();
Ext.define('erp.controller.common.FormsDoc', {
    extend: 'Ext.app.Controller',
    views:['common.FormsDoc.FormsDoc','common.FormsDoc.FormsDocTree','common.FormsDoc.FormsDocTreeGrid','core.trigger.TextAreaTrigger',
    'core.button.Close','core.button.Save','core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({    
		   	'erpFormsDocTree' : {
				beforeitemclick:function(tree,record,item,index,e,eOpts){
					if(e.target.className.indexOf('x-tree-expander')>-1){
						return true;
					}
					return false;
				},
				select:function(treeview,record,index){
					var tree = Ext.getCmp("FormsDocTree");					
					var grid = Ext.getCmp("FormsDocTreeGrid");
					var savebtn = Ext.getCmp("saveFileButton");
					isSearch = false;
					
					grid.store.load({
						params : {
							caller: caller,
							formsid: formsid,
							id : record.get("fd_id"),
							kind : 0
						},
						callback:function(records, operation, success){
							var text = grid.store.getProxy().getReader().rawData ;
							if(success){
								var res = new Ext.decode(operation.response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
								}
							}
						}
					});
					grid.nodeId = record.get("fd_id");
				}
			},
			'erpFormsDocTreeGrid':{
				selectionchange:function(self,selected,eOpts){			
					var deletebtn = Ext.getCmp('deleteFileButton');
					var uploadbtn = Ext.getCmp('FormsDocTreeGrid').down('filefield');
					var readfilebtn = Ext.getCmp('readFileButton');
					if(selected.length>0){
						if(selected[selected.length-1].data.fd_filepath){
							readfilebtn.setDisabled(false);
						}else{
							readfilebtn.setDisabled(true);
						}
						deletebtn.setDisabled(false);
						if(uploadbtn){
							uploadbtn.setDisabled(false);
						}
					}else{
						readfilebtn.setDisabled(true);
						deletebtn.setDisabled(true);
						if(uploadbtn){
							uploadbtn.setDisabled(true);
						}
					}				
				}
			},
			'erpFormsDocTreeGrid gridscroller[dock=bottom]':{
				afterrender:function(scroll){
					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.collapse();
					}
				}
			},
			'erpFormsDocTreeGrid button[id=readFileButton]' : {
				click : function(btn) {
					var fileList = Ext.getCmp('FormsDocTreeGrid');
					var select = fileList.selModel.lastSelected;
					var path = unescape(select.data.fd_filepath);
					var id = path.substring(path.lastIndexOf(';')+1);
					path = path.substring(0,path.lastIndexOf(';'));
					var type = path.substring(path.lastIndexOf('.') + 1);
					var folderId = select.data.fd_parentid;
					fileList.readFile(id,folderId,type);
				}
			},
			'erpFormsDocTreeGrid button[id=addFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(false);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					var node = Ext.getCmp("FormsDocTree").getSelectionModel()
							.getSelection()[0];
							
					if (node) {
						var parentId = node.get('fd_id');
						if (parentId == -1) {
							Ext.Msg.alert("提示", "请先保存目录");
							return;
						}
						var virtualpath = node.get("fd_virtualpath");
						var newNode = new Object();
						var data = grid.store.data.items;
						var detno = 1;
						if(data.length>0){
							detno = data[data.length-1].data['fd_detno']+1;
						}
						newNode.data = {
							fd_name : "新建文件",
							leaf : true,
							fd_parentid : parentId,
							fd_id : -1,
							fd_virtualpath : virtualpath,
							fd_kind : 0,
							fd_remark : "",
							fd_doccode : null,
							fd_detno : detno,
							fd_formsid :formsid,
							fd_caller :caller
						};

						grid.store.loadData([newNode.data], true);
					} else {
						Ext.Msg.alert("提示", "请先选择目录");
					}

				}
			},
			'erpFormsDocTreeGrid button[id=saveFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(false);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					grid.save(grid);

				}
			},
			'erpFormsDocTreeGrid button[id=deleteFileButton]':{
				afterrender:function(btn){
					btn.setDisabled(true);
				}
			},
			'erpFormsDocTreeGrid #toolbartext':{
				afterrender:function(tlbar){
					tlbar.hide();
				}
			}
    	});
    }
});
