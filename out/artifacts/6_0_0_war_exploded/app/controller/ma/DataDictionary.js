Ext.QuickTips.init();
Ext.define('erp.controller.ma.DataDictionary', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'ma.DataDictionary','core.form.Panel','core.grid.Panel2','core.button.Sync','ma.DataDictionaryGrid','ma.DictPropertyGrid',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.DeleteDetail','core.button.ResAudit',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar',
	       ],
	       init:function(){
	    	   var me = this;    	
	    	   this.control({ 
	    		   'form':{
	    			   afterrender:function(form){
	    				   if(!currentRecord && tablename){
	    					   me.getTable(form,tablename);
	    					   me.getRelations(tablename);
	    				   }

	    			   }  
	    		   },
	    		   'button[itemId=save]':{
	    			   click:function(btn){
	    				   var params=me.getColumnChanges();
	    				   Ext.apply(params,me.getIndexChanges());
	    				   params.tablename=tablename;
	    				   var form=btn.ownerCt.ownerCt,values=form.getForm().getValues();
	    				   params.formStore=Ext.JSON.encode(values);
	    				   var propgrid=Ext.getCmp("propertygrid");
	    				   var param=me.getGridStore(propgrid);
	    				   param = param == null ? [] : "[" + param.toString() + "]";
	    				   params.gridStore= unescape(param);
	    				   me.FormUtil.setLoading(true);
	    				   Ext.Ajax.request({
	    					   method:'post',
	    					   url:basePath+'ma/dataDictionary/alter.action',
	    					   params:params,
	    					   callback : function(options, success, response){
	    						   me.FormUtil.setLoading(false);
	    						   if (!response) return;
	    						   var res = new Ext.decode(response.responseText);
	    						   if(res.success){
	    							   window.location.reload();
	    						   }
	    						   else if(res.exceptionInfo != null){
	    							   showError(res.exceptionInfo);return;
	    						   }

	    					   }
	    				   });
	    			   }
	    		   },
	    		   'button[itemId=add]':{
	    			   click:function(btn){
	    				   if(isbasic==1){ me.onAdd('dictionarypanel', '系统数据字典', 'jsps/ma/dataDictionary.jsp?isbasic='+isbasic);}
	    				   else if(isbasic==null||isbasic==''){
	    					   me.FormUtil.onAdd('addDic', '系统数据字典', 'jsps/ma/dataDictionary.jsp');
	    				   }
	    			   }
	    		   },
	    		   'button[itemId=refresh]':{
	    		   		click:function(btn){
	    		   		   var form=btn.ownerCt.ownerCt;
	    				   me.FormUtil.setLoading(true);
	    				   Ext.Ajax.request({
	    					   method:'post',
	    					   url:basePath+'ma/dataDictionary/refresh.action',
	    					   timeout:60000,
	    					   params:{tablename:tablename},
	    					   callback : function(options, success, response){
	    						   me.FormUtil.setLoading(false);
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.success){
	    						   		Ext.Msg.alert('提示','刷新成功');
	    						   		window.location.reload();
	    						   }else{
	    						   		Ext.Msg.alert('提示','刷新失败');
	    						   }
	    						}
	    				   });
	    			   }
	    		   },
	    		   'button[itemId=close]':{
	    			   click:function(btn){
	    				   if(isbasic){
	    					var main = parent.Ext.getCmp("dictionarypanel");
	    					 main.getActiveTab().close();
	    				   }else{
	    					   var main = parent.Ext.getCmp("content-panel");
	    					   main.getActiveTab().close();
	    				   }

	    			   }
	    		   },
	    		   'radiogroup':{
	    			   change: function(group ,newvalue,oldvalue){
	    				   if(Ext.isString(newvalue.uniqueness)){
	    					   me.isDirtyChange(newvalue.uniqueness, group.name);
	    				   }	    				  
	    			   }  
	    		   },
	    		   'gridpanel[id=grid]':{
	    			   afterrender:function(grid){
	    				   grid.plugins[1].on('beforeedit',function(e,Opts){
	    					   if(e.column.dataIndex == 'data_length'){
	    						   var record = e.record,column = e.column;
	    						   var f = record.data['data_type'];
	    						   if(f=='DATE' || f=='CLOB' || f=='TIMESTAMP'){
	    							   return false;
	    						   }
	    					   }
	    				   });
	    				   grid.plugins[1].on('edit',function(editor,e){
	    					   if(e.field=='column_name'){
	    						   var record = e.record,value=e.value;
	    						   Ext.Array.each(record.store.data.items,function(item,index){	    							   
	    							   if(item.get('column_name')==value && index!=e.rowIdx && item.get('column_name')!=null&&item.get('column_name')!=''){
	    								   Ext.Msg.alert('提示','列 '+value+' 已存在!');
	    								   e.record.reject();
	    							   }
	    						   });
	    					   }

	    				   }); 
	    			   } 
	    		   },
	    		   'gridpanel[id=index_column_grid]':{
	    			   afterrender:function(grid){
	    				   grid.plugins[0].on('edit',function(editor,e,Opts){
	    					   /*if()
	    			    		e.originalValue!=e.value
	    			    		e.record.reject();*/
	    					   var msg=me._checkIndex_col(e.value,e.record,e.rowIdx);
	    					   if(msg){
	    						   e.record.reject();
	    						   Ext.Msg.alert('提示',msg);
	    					   }
	    				   });
	    			   }
	    		   },

	    		   'button[itemId=column_add]':{
	    			   click:function(btn){
	    				   var g=btn.ownerCt.ownerCt;
	    				   g.getStore().insert(0,{data_type:'VARCHAR2',data_length:20,nullable:'Y'});
	    			   }
	    		   },
	    		   'button[itemId=column_delete]':{
	    			   click:function(btn){
	    				   var grid=btn.ownerCt.ownerCt;	    			   
	    				   if(isbasic==1){						//增加isbasic参数判断是否是标准字段
	    					var _g=btn.ownerCt.ownerCt,store=_g.getStore();	    				  
	    				   var selected=_g.getSelectionModel().getLastSelected();
	    				   if(selected) {store.remove(selected);}
	    				   }else if(isbasic==null||isbasic==''){				
	    					   var _g=btn.ownerCt.ownerCt,store=_g.getStore();	    				  
    						   var selected=_g.getSelectionModel().getLastSelected();
	    					   var _index=selected.data.column_name;
		    					  if(_index.indexOf('_USER')>-1)
		    						  store.remove(selected);		    							  
		    					  else alert("不能删除标准字段");		    					   	 		    					     					  	    					
	    				   }	    				   
	    			   }
	    		   },
	    		   'button[itemId=add_index]':{
	    			   click:function(btn){
	    				   var list=btn.ownerCt.ownerCt.down('boundlist[name=tab_indexs]'),_s=list.store,maxNum=0,_index;
	    				   Ext.Array.each(_s.data.items,function(record){
	    					   _index=record.get('index_name');
	    					   if(_index.indexOf(tablename+'_INDEX')>-1){    			    	
	    						   var s=_index.split(tablename+'_INDEX')[1];
	    						   if(s.length>0){
	    							   if(Ext.isNumeric(s)){
	    								   if(s>maxNum) maxNum=s;
	    							   }
	    						   }
	    					   }
	    				   });
	    				   maxNum=parseInt(maxNum)+1;
	    				   var data={
	    						   index_name:tablename+'_INDEX'+maxNum,
	    						   uniqueness:'NONUNIQUE',
	    						   ind_columns:[]
	    				   };
	    				   _s.insert(_s.data.items.length,data);
	    				   var _bound=Ext.ComponentQuery.query('boundlist')[0];
	    				   _bound.select(_s.data.items.length-1);
	    				   me.setIndexDisp(data);
	    			   }
	    		   },
	    		   'button[itemId=delete_index]':{
	    			   click:function(btn){
	    				   var list=btn.ownerCt.ownerCt.down('boundlist[name=tab_indexs]'),_s=list.store;
	    				   _s.remove(list.getSelectionModel().getSelection());
	    				   if(_s.data.items.length>0){
	    					   list.select(0);
	    					   me.setIndexDisp(_s.data.items[0].data);
	    				   }

	    			   }
	    		   },
	    		   'button[itemId=add_ind_column]':{
	    			   click:function(btn){
	    				   var _g=Ext.getCmp('index_column_grid'),store=_g.getStore();
	    				   var _col=me.getInd_column(store.collect('COLUMN_NAME'));
	    				   store.insert(store.data.items.length,{
	    					   COLUMN_NAME:_col,
	    					   DESCEND:'ASC'
	    				   });
	    				   var arr=new Array();
	    				   Ext.Array.each(store.data.items,function(item){
	    					   arr.push(item.data);
	    				   });
	    				   me.isDirtyChange(arr, "ind_columns");
	    			   }
	    		   },
	    		   'button[itemId=delete_ind_column]':{
	    			   click:function(btn){
	    				   var _g=Ext.getCmp('index_column_grid'),store=_g.getStore();	    				  
	    				   var selected=_g.getSelectionModel().getLastSelected();
	    				   var arr=new Array();
	    				   if(selected) store.remove(selected);
	    				   Ext.Array.each(store.data.items,function(item){
	    					   arr.push(item.data);
	    				   });
	    				   me.isDirtyChange(arr, "ind_columns");
	    			   }
	    		   },
	    		   'boundlist[name=tab_indexs]':{
	    			   itemclick:function(view,record){
	    				   me.setIndexDisp(record.data);
	    			   },
	    			   beforrender:function(list){
	    				   list.multiSelect=false;	    				 
	    			   },
	    			   afterrender:function(c){
	    				   Ext.defer(function(){
	    					   var _bound=Ext.ComponentQuery.query('boundlist')[0],_store=_bound.getStore();
	    					   if(_store.data.items.length>0 && _bound.getSelectedNodes().length==0){
	    						   _bound.select(0);
	    						   me.setIndexDisp(_store.data.items[0].data);
	    					   }
	    				   },200);
	    			   }
	    		   },
	    		   'erpSaveButton': {
	    			 /*  afterrender: function(){
	    				   me.getDetail();
	    			   },*/
	    			   click: function(btn){
	    				   me.FormUtil.onSave(this);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   var form = btn.up('form'),
	    				   grid = form.ownerCt.down('grid'), 
	    				   table = form.down('#dd_tablename').value,
	    				   key = form.down('#dd_primekey').value;
	    				   grid.store.each(function(){
	    					   if(this.get('ddd_id') == 0) {
	    						   this.set('ddd_tablename', table);
	    						   this.set('ddd_primekey', key);
	    					   }
	    				   });
	    				   me.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   if(isbasic==1){
	    				   me.onAdd('dictionarypanel', '新增数据字典', 'jsps/ma/dataDictionary.jsp?isbasic='+isbasic);}
	    				   else if(isbasic==null||isbasic==''){
	    					   me.FormUtil.onAdd('addDataDictionary', '新增数据字典', 'jsps/ma/dataDictionary.jsp');
	    				   }
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },
	    		   'erpGridPanel2': {
	    			   afterrender: function(g) {
	    				   g.plugins[0].on('beforeedit', function(args){
	    					   if(g.readOnly || (args.record.get('ddd_id') > 0 && args.field != 'ddd_description')) {// 已存在的，不允许直接界面修改
	    						   return false;
	    					   }
	    				   });
	    			   },
	    			   itemclick: function(selModel, record) {
	    				   if(record) 
	    					   selModel.ownerCt.down('erpAddDetailButton').setDisabled(false);// 可新增字段   	
	    			   }
	    		   },
	    		   'erpFormPanel textfield[name=dd_tablename]': {
	    			   change: function(field){
	    				   field.setValue(field.value.toUpperCase());
	    				   var grid = Ext.getCmp('grid');
	    				   Ext.each(grid.store.data.items, function(item){
	    					   if(item.dirty == true){
	    						   item.set('ddd_tablename', Ext.getCmp('dd_tablename').value);
	    						   item.set('ddd_updatetime', Ext.getCmp('dd_updatetime').value);
	    						   item.set('ddd_updateuser', Ext.getCmp('dd_updateuser').value);
	    						   item.set('ddd_primekey', Ext.getCmp('dd_primekey').value);
	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpFormPanel textfield[name=dd_updatetime]': {
	    			   change: function(){
	    				   var grid = Ext.getCmp('grid');
	    				   Ext.each(grid.store.data.items, function(item){
	    					   if(item.dirty == true){
	    						   item.set('ddd_tablename', Ext.getCmp('dd_tablename').value);
	    						   item.set('ddd_updatetime', Ext.getCmp('dd_updatetime').value);
	    						   item.set('ddd_updateuser', Ext.getCmp('dd_updateuser').value);
	    						   item.set('ddd_primekey', Ext.getCmp('dd_primekey').value);
	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpFormPanel textfield[name=dd_updateuser]': {
	    			   change: function(){
	    				   var grid = Ext.getCmp('grid');
	    				   Ext.each(grid.store.data.items, function(item){
	    					   if(item.dirty == true){
	    						   item.set('ddd_tablename', Ext.getCmp('dd_tablename').value);
	    						   item.set('ddd_updatetime', Ext.getCmp('dd_updatetime').value);
	    						   item.set('ddd_updateuser', Ext.getCmp('dd_updateuser').value);
	    						   item.set('ddd_primekey', Ext.getCmp('dd_primekey').value);
	    					   }
	    				   });
	    			   }
	    		   }
	    	   });
	       },
	       _checkIndex_col:function(value,record,rowIndex){
	    	   var msg=null;
	    	   Ext.Array.each(record.store.data.items,function(item,index){
	    		   if(item.get('COLUMN_NAME')==value && index!=rowIndex){
	    			   msg='列 '+value+' 在索引 '+Ext.getCmp('index_name').value+' 中只能使用一次';
	    			   return false;
	    		   }
	    	   });
	    	   return msg;
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       getTable:function(form,tablename){
	    	   if(tablename){
	    		   Ext.Ajax.request({
	    			   url : basePath + '/common/getFieldsData.action',
	    			   async: false,
	    			   params: {
	    				   caller: 'USER_OBJECTS LEFT JOIN User_Tab_Comments ON OBJECT_NAME=User_Tab_Comments.Table_Name',
	    				   fields: 'object_name,object_id,comments',
	    				   condition: "OBJECT_NAME='"+tablename+"' and OBJECT_TYPE='TABLE'"
	    			   },
	    			   method : 'post',
	    			   callback : function(opt, s, res){
	    				   var r = new Ext.decode(res.responseText);
	    				   if(r.exceptionInfo){
	    					   showError(r.exceptionInfo);return;
	    				   } else if(r.success && r.data){
	    					   form.getForm().setValues(r.data);
	    				   }
	    			   }
	    		   });  
	    	   }	    	  
	       },
	       setIndexDisp:function(data){
	    	   Ext.getCmp('uniqueness').setValue({uniqueness:data['uniqueness']});
	    	   Ext.getCmp('index_name').setValue(data['index_name']);
	    	   Ext.getCmp('index_column_grid').getStore().loadData(data['ind_columns']);
	       },
	       getSelectNode:function(_bound){
	    	   if(!_bound) _bound=Ext.ComponentQuery.query('boundlist')[0];
	    	   var selects=_bound.getSelectionModel().getSelection();
	    	   if(selects.length>0) return selects[0];
	    	   else return null;  		   
	       },
	       isDirtyChange:function(newvalue,name,record){	    	   
	    	   if(!record) record=this.getSelectNode();
	    	   if(newvalue != record.get(name)){
	    		   record.set(name,newvalue);
	    	   }
	       },
	       getInd_column:function(arr){
	    	   var store=Ext.getCmp('grid').getStore(),_column=null;	    	 
	    	   Ext.Array.each(store.data.items,function(item){
	    		   if(!Ext.Array.contains(arr,item.get('column_name'))){
	    			   _column=item.get('column_name');
	    			   return false;
	    		   }  			  
	    	   });	    	   
	    	   return _column;

	       },
	       checkTab_columns:function(){
	    	   // var grid=Ext.getCmp('grid');

	       },
	       getColumnChanges:function(){
	    	   var grid=Ext.getCmp('grid'),store=grid.getStore(),
	    	   toUpdated=store.getUpdatedRecords();
	    	   toCreated=store.getNewRecords();
	    	   toRemoved=store.getRemovedRecords();
	    	   var columparam=new Object();
	    	   if(toUpdated.length>0){
	    		   var updated=new Array();
	    		   Ext.Array.each(toUpdated,function(item){
	    			   if(item.get('column_name')!=null && item.get('column_name')!='' && item.get('data_type')!=null && item.get('comments')!=null && item.get('comments')!=''){
	    				   updated.push(item.data);
	    			   }
	    		   });
	    		   columparam['Col_update']=Ext.JSON.encode(updated);
	    	   }
	    	   if(toCreated.length>0){
	    		   var created=new Array();
	    		   var bool=false;
	    		  if(isbasic==null||isbasic==''){	   
	    		   Ext.Array.each(toCreated,function(item){
	    			   if(item.get('column_name')!=null && item.get('column_name').trim()!="" && item.get('data_type')!=null && item.get('data_type').trim()!="" && item.get('comments')!=null && item.get('comments')!=''){
	    				if(currentMaster!="UAS" && item.get('column_name').toUpperCase().indexOf("_USER")<0){
	    					bool=true;	    				    
	    				    item.data['column_name']= item.get('column_name')+"_USER";		    				
	    				}
	    				created.push(item.data);
	    			   }
	    		   }); if (bool) window.alert("非UAS标准字段，字段名将默认添加_USER作为后缀");
	    		   }else if(isbasic==1){  //管理员可以随意添加字段的判断逻辑
	    			   Ext.Array.each(toCreated,function(item){
	    			   if(item.get('column_name')!=null && item.get('column_name').trim()!="" && item.get('data_type')!=null && item.get('data_type').trim()!="" && item.get('comments')!=null && item.get('comments')!=''){	
		    				   created.push(item.data);
		    			   }
		    		   });}
	    		   columparam['Col_create']=Ext.JSON.encode(created);
	    	   }
	    	   if(toRemoved.length>0){
	    		   var removed=new Array();
	    		   Ext.Array.each(toRemoved,function(item){
	    			   if(item.get('column_name')!=null && item.get('column_name')!=' ' && item.get('data_type')!=null){
	    				   removed.push(item.data);
	    			   }
	    		   });
	    		   columparam['Col_remove']=Ext.JSON.encode(removed);
	    	   }
	    	   return columparam;
	       },
	       getIndexChanges:function(){
	    	   var _bound=Ext.ComponentQuery.query('boundlist[name=tab_indexs]')[0],store=_bound.getStore();
	    	   toUpdated=store.getUpdatedRecords();
	    	   toCreated=store.getNewRecords();
	    	   toRemoved=store.getRemovedRecords();
	    	   var indexparam=new Object();
	    	   if(toUpdated.length>0){
	    		   indexparam['Ind_update']=Ext.JSON.encode(toUpdated);
	    	   }
	    	   if(toCreated.length>0){
	    		   var created=new Array();
	    		   Ext.Array.each(toCreated,function(item){

	    			   if(item.get('ind_columns').length>0){
	    				   created.push(item.data);
	    			   }
	    		   });
	    		   indexparam['Ind_create']=Ext.JSON.encode(created);
	    	   }
	    	   if(toRemoved.length>0){
	    		   var remove=new Array();
	    		   Ext.Array.each(toRemoved,function(item){	    			   
	    			   remove.push(item.data);
	    		   });
	    		   indexparam['Ind_remove']=Ext.JSON.encode(remove);
	    	   }
	    	   return indexparam;
	       },
	       getRelations:function(tablename){
	    	   var me=this;
	    	   Ext.Ajax.request({
	    		   url:basePath+'ma/getDatarelations.action',
	    		   method:'get',
	    		   params:{tablename:tablename},
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success && r.relations &&  r.relations.length>0 ){
	    				   me.showRelTab(me.formatRelations(r));
	    			   }
	    		   }

	    	   });
	       },
	       formatRelations:function(r){
	    	   var arr=new Array(),o,groupname,relations=r.relations,col_comments=r.relations_col_comments,tab_comments=r.relations_tab_comments;
	    	   Ext.Array.each(relations,function(item){	
	    		   Ext.Array.each(tab_comments,function(tab){
	    			   if(tab.TABLE_NAME==item.table_name_y){
	    				   groupname=item.table_name_y+'('+tab.COMMENTS+')';
	    				   return false;
	    			   }
	    		   });
	    		   if(item.col_x_1){
	    			   o=new Object();
	    			   o.col_x=item.col_x_1;
	    			   o.col_y=item.col_y_1;
	    			   Ext.Array.each(r.relations_col_comments,function(col){
	    				   if(col.COLUMN_NAME==item.col_x_1 && col.TABLE_NAME==item.table_name_x){
	    					   o.desc_x=col.COMMENTS;
	    					   o.type_x=col.DATA_TYPE;
	    				   }
	    				   if(col.COLUMN_NAME==item.col_y_1 && col.TABLE_NAME==item.table_name_y){
	    					   o.desc_y=col.COMMENTS;
	    					   o.type_y=col.DATA_TYPE;
	    				   }
	    			   });
	    			   o.tab_y=groupname;
	    			   arr.push(o);
	    		   }
	    		   if(item.col_x_2){
	    			   o=new Object();
	    			   o.col_x=item.col_x_2;
	    			   o.col_y=item.col_y_2;
	    			   Ext.Array.each(r.relations_col_comments,function(col){
	    				   if(col.COLUMN_NAME==item.col_x_2 && col.TABLE_NAME==item.table_name_x){
	    					   o.desc_x=col.COMMENTS;
	    					   o.type_x=col.DATA_TYPE;
	    				   }
	    				   if(col.COLUMN_NAME==item.col_y_2 && col.TABLE_NAME==item.table_name_y){
	    					   o.desc_y=col.COMMENTS;
	    					   o.type_y=col.DATA_TYPE;
	    				   }
	    			   });
	    			   o.tab_y=groupname;
	    			   arr.push(o);
	    		   }
	    	   });
	    	   return arr;
	       },
	       showRelTab:function(data){
	    	   var tabP=Ext.getCmp('dictab'),_m=this;
	    	   tabP.insert(2,{
	    		   title:'关联表',
	    		   xtype:'gridpanel',
	    		   id:'relationgrid',
	    		   columnLines:true,
	    		   columns:[{ 
	    			   text:'基础表',
	    			   cls: "x-grid-header-1",
	    			   columns:[{
	    				   text:'列名',
	    				   dataIndex:'col_x',
	    				   cls: "x-grid-header-2",
	    				   width:150,
	    				   fixed :true
	    			   },{
	    				   text:'类型',
	    				   dataIndex:'type_x',
	    				   //cls: "x-grid-header-1",
	    				   width:80,
	    				   fixed :true
	    			   },{
	    				   text:'注释',
	    				   dataIndex:'desc_x',
	    				   // cls: "x-grid-header-1",
	    				   width:200,
	    				   fixed :true 
	    			   }]
	    		   },{
	    			   text:'关联表',
	    			   cls: "x-grid-header-1",
	    			   columns:[{
	    				   text:'字段',
	    				   dataIndex:'col_y',
	    				   // cls: "x-grid-header-1",
	    				   width:150,
	    				   fixed :true
	    			   },{
	    				   text:'类型',
	    				   dataIndex:'type_y',
	    				   //cls: "x-grid-header-1",
	    				   width:80,
	    				   fixed :true
	    			   },{
	    				   text:'注释',
	    				   dataIndex:'desc_y',
	    				   // cls: "x-grid-header-1",
	    				   width:200,
	    				   fixed :true 
	    			   }]

	    		   },{
	    			   dataIndex:'tab_y',
	    			   cls: "x-grid-header-1",
	    			   width:0
	    		   }     
	    		   ],
	    		   store:Ext.create('Ext.data.Store', {
	    			   fields: [ {name: 'col_x'},{name:'desc_x'},{name:'type_x'},
	    			             {name:'col_y'},{name:'desc_y'},{name:'type_y'},{name:'tablerelation'},{name:'tab_x'},{name:'tab_y'}],
	    			             data:data,
	    			             groupers:['tab_y']
	    		   }),
	    		   features: [{
	    			   //id: 'group',
	    			   ftype: 'grouping',
	    			   groupHeaderTpl: '关联表:<a href="#">{name}</a>',
	    			   getFeatureTpl: function(values, parent, x, xcount) {
	    				   var me = this;	
	    				   return [
	    				           '<tpl if="typeof rows !== \'undefined\'">',
	    				           // group row tpl
	    				           '<tr class="' + Ext.baseCSSPrefix + 'grid-group-hd ' + (me.startCollapsed ? me.hdCollapsedCls : '') + ' {hdCollapsedCls}"><td class="' + Ext.baseCSSPrefix + 'grid-cell" align=center colspan="' + parent.columns.length + '" ><div class="' + Ext.baseCSSPrefix + 'grid-cell-inner"><div class="' + Ext.baseCSSPrefix + 'grid-group-title">{collapsed}' + me.groupHeaderTpl + '</div></div></td></tr>',
	    				           // this is the rowbody
	    				           '<tr id="{viewId}-gp-{name}" class="' + Ext.baseCSSPrefix + 'grid-group-body ' + (me.startCollapsed ? me.collapsedCls : '') + ' {collapsedCls}"><td colspan="' + parent.columns.length + '">{[this.recurse(values)]}</td></tr>',
	    				           '</tpl>'
	    				           ].join('');
	    			   },
	    			   onGroupClick: function(view, group, idx, foo, e) {
	    			      var _table=idx.substring(0,idx.indexOf('('));
	    			      _m.FormUtil.onAdd(_table,'系统数据字典',basePath+'jsps/ma/dataDictionary.jsp?formCondition=object_nameIS'+_table);
	    			    },
	    			   enableGroupingMenu: false
	    		   }]
	    	   });
	       },
	       getGridStore:function(grid){
	       		var jsonGridData = new Array();
				var s = grid.getStore().data.items;//获取store里面的数据
				var dd;
				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
					var data = s[i].data;
					dd = new Object();
					if(s[i].dirty){
						Ext.each(grid.columns, function(c){
							if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
						});
						jsonGridData.push(Ext.JSON.encode(dd));
					}
				}
				return jsonGridData;
	       },
	       onAdd: function(panelId, title, url){
			   var main = this.getMain();
	    		if(main){
	    			panelId = panelId == null
	    			? Math.random() : panelId;
	    			var panel = Ext.getCmp(panelId); 
	    			if(!panel){ 
	    				var value = "";
	    				if (title.toString().length>5) {
	    					value = title.toString().substring(0,5);	
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
	    				this.openTab(panel, panelId);
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
	    	getMain: function(){
	    		var main = Ext.getCmp("dictionarypanel");
	    		if(!main)
	    			main = parent.Ext.getCmp("dictionarypanel");
	    		if(!main)
	    			main = parent.parent.Ext.getCmp("dictionarypanel");
	    		return main;
	    	},
	    	openTab : function (panel,id){ 
	    		var o = panel.id; 
	    		var main = this.getMain();
	    		var tab = main.getComponent(o); 
	    		if (tab) { 
	    			main.setActiveTab(tab); 
	    		} else if(typeof panel!="string"){ 
	    			panel.id = o; 
	    			var p = main.add(panel); 
	    			main.setActiveTab(p); 
	    		} 
	    	} 
});