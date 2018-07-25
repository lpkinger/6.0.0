Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.ItemsValueSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fs.credit.ItemsValueSet','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': { 
        			beforereconfigure : function(grid,store, columns, oldStore, oldColumns) {
        				var delBtn = Ext.getCmp('deletebutton');
    					if(delBtn){
    						if(store.data.items.length<1){
        						delBtn.setDisabled(true);
        					}else{
        						delBtn.setDisabled(false);
        					}
    					} 
    					if(ctid!=null&&store.data.items.length<1){
    						 me.addEmptyData(store,ctid,1);
    					}
    					
        				Ext.Array.each(columns,function(column){
        						if(column.dataIndex=='ctc_display'){
        							column.flex = 5;
        							
        						}else{
        							column.flex = 1;
        						}	
		   				});
        			},
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);   					
        			}
        		},
        		'erpSaveButton': {
        			click: function(){
        				me.Save();
        			}
        		},        
        		'erpDeleteButton': {
	                click: function(btn) {
	                   me.Delete();
	                }
	            }
        	});
        },
        onGridItemClick: function(selModel, record){//grid行选择
			var me = this;
			var grid = selModel.ownerCt;
			if(grid && !grid.readOnly && !grid.NoAdd){
				var index = grid.store.indexOf(record);
				if(index == grid.store.indexOf(grid.store.last())){
					var ctid = record.data['ctc_ctid'];
					var init = parseInt(record.data['ctc_value']==null?0:record.data['ctc_value'])+1;
					if(ctid>0&&init>0){
						me.addEmptyData(grid.store,ctid,init);//就再加10行
					}
		    	}
				var btn = grid.down('erpDeleteDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('erpAddDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('copydetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('pastedetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('updetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('downdetail');
				if(btn)
					btn.setDisabled(false);
				if(grid.down('tbtext[name=row]')){
					grid.down('tbtext[name=row]').setText(index+1);
				}
			}
        },
        addEmptyData: function(store,ctid,init){
        	var datas = new Array();
        	for(var i=0;i<10;i++){
				var o = new Object();
				o.ctc_ctid=ctid;
				o.ctc_id=null;
				o.ctc_display=null;
				o.ctc_value=i+init;
				o.ctc_rate=null;
				o.ctc_maxlevel=null;
				datas.push(o);
			}
			if(init>1){
				store.loadData(datas,true);
			}
			store.on('datachanged', function(store, eOpts ){
				var data = store.data.items;
				for(var i=0;i<data.length;i++){
					if(!data[i].data['ctc_ctid']||data[i].data['ctc_ctid']==''||data[i].data['ctc_ctid']=='0'){
						store.removeAll();
						store.loadData(datas);
					}
				}
            });
			
        },
        Save:function(){ 
        	var datas = this.GridUtil.getGridStore();   
        	if(datas.length==0){
        		Ext.Msg.alert('警告','未修改数据！');
        		return;
        	}
        	param = "[" + datas.toString() + "]";
			Ext.Ajax.request({
				url:basePath + 'fs/credit/saveItemsValue.action',
				params:{			
					datas:param
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						Ext.Msg.alert('提示','保存成功！',function(){
							window.location.reload();
						});
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
        },
        Delete:function(){   
        	Ext.Msg.confirm("删除","请确认是否删除项目值设置？",function(btn){
	    		if(btn=='yes'){
					Ext.Ajax.request({
						url:basePath + 'fs/credit/deleteItemsValue.action',
						params:{			
							id:ctid
						},
						method:'post',
						async: false,
						callback:function(options,success,resp){
							var res = new Ext.decode(resp.responseText);
							if(res.success){
								Ext.Msg.alert('提示','删除成功！',function(){
									var win = parent.Ext.getCmp('win');
									win.close();
								});						
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);	
							}
						}
					});
	    		}
    		});
        }
});