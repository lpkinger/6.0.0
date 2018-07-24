Ext.ns('DOC');
DOC = {
		columns:{
			LinkColumns:[{
				text:'ID',
				dataIndex:'dl_id',
				width:0,
			},{
				cls : "x-grid-header-1",
				text: '文档',
				dataIndex: 'dl_name',
				flex: 1,
				readOnly:true,
				renderer:function(val, meta, record){
				}
			},{
				cls : "x-grid-header-1",
				text: '链接目录',
				dataIndex: 'dl_manage',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '创建人',
				dataIndex: 'dpc_scan',
				flex: 1
			},{
				cls : "x-grid-header-1",
				text:'创建世间',
				dataIndex: 'dpc_create',
				flex: 1,
				readOnly:true
			}],
			PowerColumns:[{
				text:'ID',
				dataIndex:'dp_id',
				width:0,
				border:0
			},{
				cls:'x-grid-header-1',
				text:'角色类型',
				dataIndex:'dp_type',
				summaryType:'count'
			},{
				cls : "x-grid-header-1",
				text: '人员/部门/岗位',
				dataIndex: 'dp_name',
				flex: 1
			},{
				cls : "x-grid-header-1",
				text: '管理',
				dataIndex: 'dp_control',
				flex: 1,
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			},{
				cls : "x-grid-header-1",
				text: '浏览',
				dataIndex: 'dp_see',
				flex: 1,
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			},{
				cls : "x-grid-header-1",
				text:'创建',
				dataIndex: 'dp_save',
				flex: 1,
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			},{
				cls : "x-grid-header-1",
				text:'阅读',
				dataIndex:'dp_read',
				flex:1,
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			},{
				cls:'x-grid-header-1',
				text:'删除',
				dataIndex:'dp_delete',
				flex:1,
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			}/*,{
				cls : "x-grid-header-1",
				text:'打印',
				dataIndex: 'dp_print',
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
				}
			}*/,{
				cls:'x-grid-header-1',
				text:'下载',
				readonly:true,
				dataIndex:'dp_download',
				xtype: 'checkcolumn',
				editor: {
					xtype: 'checkbox',
					cls: 'x-grid-checkheader-editor'
			    }
			},{
				xtype:'actioncolumn',
				width:50,
				//width:0,
				text: '操作',
				items: [{
					icon: 'resources/images/icon-edit.gif',
					padding:'0 0 0 5',
					style:'padding:"0 10 0 10"',
					tooltip: '修改',
					handler: function(grid, rowIndex, colIndex) {
						var me = this;
    				    var selected = Ext.getCmp('doctree').getSelectionModel().selected;
    				    if(selected.items < 1){
    					    showResult('提示','请选择文件夹',me);
    					    return;
    				    }
    				    var folderId = selected.items[0].data.id
    				    Ext.Ajax.request({
    				    	url: basePath + 'oa/doc/checkPower.action',
	    					params: {
	    						folderId: folderId,
	    						type:	"dp_control",
	    					},
	    					success: function(response){
	    						var res = Ext.decode(response.responseText);
	    						if(res){
	    							var rec = grid.getStore().getAt(rowIndex);
	    							var data=rec.data;
	    							Ext.Array.each(grid.ownerCt.powerSet,function(p){
	    									data[p]=data[p]?1:0;					
	    							});
	    							var me=this;
	    							delete data['dp_type'];
	    							Ext.Ajax.request({//拿到form的items
	    								url : basePath + 'doc/updatePowerSet.action',
	    								params: {
	    								  param:unescape(escape(Ext.JSON.encode(data)))
	    								},
	    								method : 'post',
	    								callback : function(options, success, response){
	    									 showResult('提示','修改成功!',me);
	    									 grid.ownerCt.loadNewStore();
	    								}
	    							});
	    						}else{
	    							showResult('提示','您不具有该文件夹的管理权限',me);
	   	    					   	return;
	    						}
	    					}
    				    });
					}
				},{
					icon: 'resources/images/icon-delete.gif',
					tooltip: '删除',
					style:'margin:0 0 0 10px',
					handler: function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var me=this;
						//校验是否具有该文件夹的管理权限
						var selected = Ext.getCmp('doctree').getSelectionModel().selected;
    				    if(selected.items < 1){
    					    showResult('提示','请先选择左侧的文件夹',me);
    					    return;
    				    }
    				    var folderId = selected.items[0].data.id;
    				    Ext.Ajax.request({
    				    	url: basePath + 'oa/doc/checkPower.action',
	    					params: {
	    						folderId: folderId,
	    						type:	"dp_control",
	    					},
	    					success: function(response){
	    						var res = Ext.decode(response.responseText);
	    						if(res){
	    							Ext.Ajax.request({//拿到form的items
	    								url : basePath + 'doc/deletePowerSet.action',
	    								params:{
	    									 param:unescape(escape(Ext.JSON.encode(rec.data)))
	    								},
	    								method : 'post',
	    								async:false,
	    								callback : function(options, success, response){
	    									 showResult('提示','删除成功!',me);
	    									 grid.ownerCt.loadNewStore();
	    								}
	    							});
	    						}else{
	    							showResult('提示','您不具有该文件夹的管理权限',me);
	   	    					   	return;
	    						}
	    					}
    				    });
					}
				}]
			},{
				cls:'x-grid-header-1',
			    width:0,
				dataIndex:'dp_table'
			},{
				cls:'x-grid-header-1',
				text:'表',
				readonly:true,
				width:0,
				flex:0,
				dataIndex:'dp_table'
			},{
				cls:'x-grid-header-1',
				text:'所属ID',
				readonly:true,
				width:0,
				flex:0,
				dataIndex:'dp_dclid'
			}],
			HistoryColumns:[{
				text:'ID',
				dataIndex:'dv_id',
				width:0,
			},{
				cls : "x-grid-header-1",
				text: '操作',
				xtype:'actioncolumn',			
				flex: 0.5,
				items:[{
					icon: 'resources/images/icon-download.gif',
					tooltip: '下载',
					handler: function(grid, rowIndex, colIndex) {	
						var me = this;
						var select=grid.getStore().getAt(rowIndex);
						if (!Ext.fly('ext-attach-download')) {  
							var frm = document.createElement('form');  
							frm.id = 'ext-attach-download';  
							frm.name = id;  
							frm.className = 'x-hidden';
							document.body.appendChild(frm);  
						}
						Ext.Ajax.request({
							url: basePath + 'common/download.action?fileName=' + select.data.dv_name,
							method: 'post',
							form: Ext.fly('ext-attach-download'),
							isUpload: true,
							params: {
								escape : 1,
								path : unescape(select.data.dv_filepath)
							}
						});
					}

				}/*,{
					icon: 'resources/images/icon-withdraw.gif',
					tooltip: '回退',
					handler: function(grid, rowIndex, colIndex) {
					}
				}*/]
			},{
				cls : "x-grid-header-1",
				text: '修订号',
				dataIndex: 'dv_detno',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '修改说明',
				dataIndex: 'dv_explain',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text:'操作人',
				dataIndex:'dv_man',
				flex:1,
				readOnly:true
			},{
				cls:'x-grid-header-1',
				text:'操作时间',
				dataIndex:'dv_time',
				flex:1,
				readOnly:true,
				xtype:"datecolumn",
				format:"Y-m-d H:i:s"
			}],
			ReviewColumns:[{
				cls : "x-grid-header-1",
				text: '操作',
				xtype:'actioncolumn',			
				flex: 0.1,
				items:[{
					icon: 'resources/images/icon-delete.gif',
					tooltip: '删除',
					handler: function(grid, rowIndex, colIndex) {	       
					}

				}]
			},{
				text:'ID',
				dataIndex:'dr_id',
				width:0,
			},{
				cls : "x-grid-header-1",
				text: '评论人',
				dataIndex: 'dr_man',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '评论内容',
				dataIndex: 'dr_remark',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '评论时间',
				dataIndex: 'dr_date',
				xtype:'datecolumn',
				format:'Y-m-d H:i:s',
				flex: 1
			}],
			LogColumns:[{
				text:'ID',
				dataIndex:'dll_id',
				width:0,
			},{
				cls : "x-grid-header-1",
				text: '操作人',
				dataIndex: 'dll_man',
				flex: 1,
				readOnly:true,
				renderer:function(val, meta, record){
				}
			},{
				cls : "x-grid-header-1",
				text: '日志',
				dataIndex: 'dll_message',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '时间',
				dataIndex: 'dll_time',
				flex: 1
			}],
			RelateColumns:[{
				text:'ID',
				dataIndex:'dlr_id',
				width:0,
			},{ 
				text:'操作',
				xtype:'actioncolumn',			
				flex: 0.1,
				items:[{
					icon: 'resources/images/icon-delete.gif',
					tooltip: '删除',
					handler: function(grid, rowIndex, colIndex) {	       
					}
				}]
			},{
				cls : "x-grid-header-1",
				text: '关联文档',
				dataIndex: 'dl_name',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '',
				dataIndex: 'dlr_dlid',
				width:0,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '目录',
				dataIndex: 'dl_virtualpath',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '时间',
				dataIndex: 'dlr_date',
				xtype:'datecolumn',
				format:'Y-m-d H:s:i',
				flex: 1
			}],
			BorrowColumns:[{
				text:'ID',
				dataIndex:'db_id',
				width:0,
			},{
				cls : "x-grid-header-1",
				text: '借出人',
				dataIndex: 'db_auditman',
				flex: 1,
				readOnly:true,
				renderer:function(val, meta, record){
				}
			},{
				cls : "x-grid-header-1",
				text: '借给',
				dataIndex: 'db_persons',
				flex: 1,
				readOnly:true
			},{
				cls : "x-grid-header-1",
				text: '收回时间',
				dataIndex: 'db_backtime',
				flex: 1
			},{
				cls : "x-grid-header-1",
				text:'剩余时间',
				dataIndex: 'db_remain',
				flex: 1,
				readOnly:true
			}]
		},
		fields:{
			PowerFields:[{
				name: 'dp_id',
				type: 'number'
			},{
				name:'dp_name',
				type:'string'
			},{
				name: 'dp_control',
				type:'number'
			},{
				name:'dp_see',
				type:'bool'
			},{
				name:'dp_save',
				type:'bool'
			},{
				name:'dp_read',
				type:'bool'
			},{
				name:'dp_delete',
				type:'bool'
			},{
				name:'dp_print',
				type:'bool'
			},{
				name:'dp_download',
				type:'bool'
			},{
				name:'dp_upload',
				type:'bool'
			},{
				name:'dp_type',
				type:'string'
			},{
				name:'dp_table',
				type:'string'
			},{
				name:'dp_table',
				type:'string'
			},{
				name:'dp_dclid',
				type:'number'
			}],
			VersionFields:[{
				name: 'dv_id',
				type: 'number'
			},{
				name:'dv_dlid',
				type:'number'
			},{
				name:'dv_detno',
				type:'number'
			},{
				name: 'dv_name',
				type: 'string'
			},{
				name:'dv_filepath',
				type:'string'
			},{
				name:'dv_man',
				type:'string'
			},{
				name:'dv_time',
				type:'date'
			}],
			ReviewFields:[{
				name: 'dr_id',
				type: 'number'
			},{
				name:'dr_dlid',
				type:'number'
			},{
				name: 'dr_remark',
				type: 'string'
			},{
				name:'dr_man',
				type:'string'
			},{
				name:'dr_date',
				type:'date'
			}]

		}				
};