Ext.define('erp.controller.sysmng.step.MessageDetailController', {
	extend: 'Ext.app.Controller',
	views:['sysmng.message.messagedetail.ViewPort',
			'sysmng.message.messagedetail.MessageForm',
			'sysmng.message.messagedetail.MessageGrid',
			'core.button.Save','core.button.Update','core.button.Add',
			'core.button.Delete','core.toolbar.Toolbar',
			'core.trigger.DbfindTrigger','core.form.MonthDateField',
			'core.trigger.HtmlEditorTrigger','core.trigger.TextAreaTrigger'],
	init:function(){
		var me=this;
		me.FormUtil=Ext.create('erp.util.FormUtil');
    	me.GridUtil=Ext.create('erp.util.GridUtil');
    	me.BaseUtil=Ext.create('erp.util.BaseUtil');
		this.control({
			'MessageForm':{
				afterrender:function(){
				
				}
				
			},			
			'#recorder':{
				afterrender:function(v,b){					
					var recorder=Ext.getCmp("recorder");
					recorder.setValue(em_name);
				}
				
			},
			'#gridpanel':{
				afterrender:function(selModel, record){
					var  messagegrid=Ext.getCmp("gridpanel");					
					var length=messagegrid.store.data.length;
					if("0"==length||0==length){						
						me.add10EmptyItems(messagegrid);
						
					}
				},
				itemclick:function(a,b,c,d,e,f){
				
					var clickItems=d+1;
					var  messagegrid=Ext.getCmp("gridpanel");					
					var length=messagegrid.store.data.length;				
					if(clickItems==length){						
						me.add10EmptyItems(messagegrid);
						
					}
					me.GridUtil.onGridItemClick(a, b);	
			
			
			
			}
	/*		itemclick:function(selModel, record){		
				me.GridUtil.onGridItemClick(selModel, record);	
			}*/
					
			},
			
			
			'erpSaveButton':{
				afterrender:function(fn){
					formCondition = getUrlParam('formCondition');				
					if(formCondition != null && formCondition != ''){
						fn.hide();
					}
			
				},
				click:function(){
					var form=Ext.getCmp("form");
					var formData= form.getForm().getValues();				
					var r=unescape(escape(Ext.JSON.encode(formData)))
					var grid=Ext.getCmp("gridpanel");
					var gridData=me.GridUtil.getGridStore(grid);
					params = unescape("[" + gridData.toString() + "]");	
					this.saveData(r,params);
					
				}
			},
			'erpUpdateButton':{
				afterrender:function(fn){
					formCondition = getUrlParam('formCondition');
					if(formCondition == null || formCondition == ''){
						fn.hide();
					}
			
				},
				click:function(){
					var form=Ext.getCmp("form");
					var formData= form.getForm().getValues(false, true);
					var r=unescape(escape(Ext.JSON.encode(formData)))					
					var grid=Ext.getCmp("gridpanel");
					var gridData=me.GridUtil.getGridStore(grid);					
					var gridData1=new Array();
					var gridData2=new Array();					
					Ext.Array.each(gridData,function(v){						
						if(Ext.decode(v).mr_id=='0'){
							 gridData1.push(v);
						}else{
							gridData2.push(v);
						}
					
					});					
					params1 = unescape("[" + gridData1.toString() + "]");
					params2 = unescape("[" + gridData2.toString() + "]");
					this.updataData(r,params1,params2);	
					
				}
		
			},
			'erpAddButton':{
				afterrender:function(fn){
					formCondition = getUrlParam('formCondition');
					if(formCondition == null || formCondition == ''){
						fn.hide();
					}
				},
				click:function(btn){					
					me.FormUtil.onAdd('addMessageDetail', '新增', 'jsps/sysmng/messagedetail.jsp');
				}
				
			},
			'erpDeleteButton':{
				afterrender:function(fn){
					formCondition = getUrlParam('formCondition');
					if(formCondition == null || formCondition == ''){
						fn.hide();
					}
				},
				click:function(){
					
					var id=Ext.getCmp('id').value;				
					this.deleteData(id);
				}	
			},
			'erpDeleteDetailButton':{
				click:function(btn){
					var grid = btn.grid ||btn.ownerCt.ownerCt;
					var records = grid.selModel.getSelection();			
					var id=records[0].data.mr_id;					
					this.toolbarDelete(id);
				}
				
			}
		});
	},
	
	saveData:function(r,gridData){
		Ext.Ajax.request({
	        url : basePath + 'sysmng/saveData.action',
	        params: {	        		 
	        		formData:r,
	        		gridData:gridData,  
	        	},
	        method : 'post',
	        callback : function(options,success,response){	        			        	
	        	var res = new Ext.decode(response.responseText);
	        	if(res.exceptionInfo != null){
	        		showError(res.exceptionInfo);return;
	        	}else{	        			        		
	        		Ext.Msg.show({ 
						title : '系统提示', 
						msg : '保存成功', 
						buttons: Ext.Msg.OK,
						fn:function(btn, text) {
							if(btn=='ok'){						
								window.location.href = basePath +'jsps/sysmng/messagedetail.jsp?formCondition=mm_idIS'+res.formid+'&gridCondition=mm_idIS'+res.formid;
								
							}
						}
					}); 	
	        		}
	        	}
	        });
		
	},
	updataData:function(r,params1,params2){
		
		Ext.Ajax.request({
	        url : basePath + 'sysmng/updateData.action',
	        params: {	        		 
	        		formData:r,
	        		gridData1:params1,
	        		gridData2:params2
	        	},
	        method : 'post',
	        callback : function(options,success,response){	        			        	
	        	var res = new Ext.decode(response.responseText);
	        	if(res.exceptionInfo != null){
	        		showError(res.exceptionInfo);return;
	        	}else{	        			        		
	        		Ext.Msg.show({ 
						title : '系统提示', 
						msg : '更新成功', 
						buttons: Ext.Msg.OK,
						fn:function(btn, text) {
							if(btn=='ok'){
								window.location.reload();
								
							}
						}
   						
					}); 
	        	}}
					
	        		
});
	        	
		
	},
	toolbarDelete:function(id){
		
		me=this;
		Ext.Ajax.request({
	        url : basePath + 'sysmng/toolbarDelete.action',
	        params: {	        		 
	        		id:id       
	        	},
	        method : 'post',
	        callback : function(options,success,response){	        			        	
	        	var res = new Ext.decode(response.responseText);
	        	if(res.exceptionInfo != null){
	        		showError(res.exceptionInfo);return;
	        	}else{	        			        		
	        		Ext.Msg.show({ 
						title : '系统提示', 
						msg : '删除成功', 
						buttons: Ext.Msg.OK,
							fn:function(btn, text) {							
							if(btn=='ok'){
								window.location.reload();
							}
						}
					}); 
				
	        		}
	        	}
	        });
		
	
		
	},
	deleteData:function(id){
		me=this;
		Ext.Ajax.request({
	        url : basePath + 'sysmng/deleteData.action',
	        params: {	        		 
	        		id:id       
	        	},
	        method : 'post',
	        callback : function(options,success,response){	        			        	
	        	var res = new Ext.decode(response.responseText);
	        	if(res.exceptionInfo != null){
	        		showError(res.exceptionInfo);return;
	        	}else{
	        		Ext.Msg.show({ 
						title : '系统提示', 
						msg : '删除成功', 
						buttons: Ext.Msg.OK, 
						fn:function(){ 
							var main = parent.Ext.getCmp("content-panel"); 					
							if (main) {								
								main.getActiveTab().close();								
							} 
							
						},
						closable: false 
					}); 
	        		}
	        	}
	        });
		
	},
	add10EmptyItems: function(grid, count, append){
		var store = grid.store, 
			items = store.data.items, arr = new Array();
		var detno = grid.detno;
		count = count || 10;
		append = append === undefined ? true : false;
		if(typeof grid.sequenceFn === 'function')
			grid.sequenceFn.call(grid, count);
		else {
			if(detno){
				var index = items.length == 0 ? 0 : Number(store.last().get(detno));
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o[detno] = index + i + 1;
					o['mr_iscombine']=-1;
					o['mr_isused']=-1;					
					arr.push(o);
				}
			} else {
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o['mr_iscombine']=-1;
					o['mr_isused']=-1;						
					arr.push(o);
				}
			}
			store.loadData(arr, append);
			var i = 0;
			store.each(function(item, x){
				if(item.index) {
					i = item.index;
				} else {
					if (i) {
						item.index = i++;
					} else {
						item.index = x;
					}
				}
			});
		}
	},
});