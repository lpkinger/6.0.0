Ext.QuickTips.init();
Ext.define('erp.controller.common.datalistFilter', {
	extend: 'Ext.app.Controller',
	views:['common.datalistFilter.ConContainer','common.datalistFilter.Viewport','common.datalistFilter.TreePanel',
	       'common.datalistFilter.TabPanel'],
	       init:function(){
	    	   var me=this;
	    	   this.control({
	    		   'FilterTreePanel': {
	    			   afterrender:function(){
	    				   me.getDataListFilterName(me.renderConditionPanel);
	    			   },
	    			   select:function(node,r){
	    				   if (r.isLeaf()) {
	    					   var id=r.data.id;
	    					   me.getTreeNodeData(id);
	    					   Ext.getCmp("setDefaultButton").setValue(r.isdefault);
	    					   Ext.getCmp("saveButton").enable();
	    					   Ext.getCmp("deleteButton").enable();
	    					   Ext.getCmp("saveAsButton").enable();
	    				   }else{
	    				   		Ext.getCmp("saveButton").disable();
	    				   		Ext.getCmp("deleteButton").disable();
	    				   		Ext.getCmp("saveAsButton").disable();
	    				   		Ext.getCmp("clearconditionsp").fireEvent("click");
	    				   }
	    			   }
	    		   },
	    		   '#conditionpanel':{
	    			   beforerender:function(p){
	    				   var gconf=me.getGridConf();
	    				   var FieldStore=new Ext.data.Store({
	    					   fields: ['field', 'text','originalxtype'],
	    					   data:gconf}); 
	    				   p.FieldStore=FieldStore;                      		  
	    			   }	
	    		   },
	    		   '#deleteButton':{
	    		   	   afterrender:function(btn){
	    		   	   		btn.disable();
	    		   	   },
	    			   click:function(){				
	    				   var TreePanel=Ext.getCmp('TreePanel');
	    				   var checked=TreePanel.getView().getSelectionModel().getSelection();
	    				   if(checked.length==0){
	    					   Ext.Msg.alert("提醒","请选择要删除的方案!");	
	    				   }else{
	    					   var id=checked[0].data.id;
	    					   if(id==null || id==''||id==undefined){
	    						   showError("请选择正确的方案!");
	    						   return;
	    					   }else{
	    						   me.deleteTreeNode(id);
	    					   }	
	    				   }		
	    			   }
	    		   },
	    		   '#sureButton':{
	    			   click:function(){
	    				   var setDefaultButton=Ext.getCmp('setDefaultButton'),TreePanel=Ext.getCmp('TreePanel');
	    				   var caller = getUrlParam('caller');
	    				   var checked=TreePanel.getView().getSelectionModel().getSelection();
	    				   var conpanel=Ext.getCmp('conditionpanel');
	    				   var grid=parent.Ext.getCmp(gridId);
	    				   var condition='';
	    				   var items = conpanel.items.items;
	    				   if(items.length<=0){
	    				   		showError('筛选条件存在空值!');
	    		   				return;
	    				   }
	    				   me.cleanHeaderValue(grid);
	    				  var flag=true; 
	    				  for(var i=0;i<items.length;i++){
	    				  	for(var j=i+1;j<items.length;j++){
	    				  		if(items[i].items.items[0].value==items[j].items.items[0].value){
	    				  			showError('筛选条件存在重复!');
	    		   					flag=false;
	    		   					return;
	    				  		}
	    				  	}
	    				  }
	    				   Ext.Array.each(items,function(item,index){
	    				   		if(item.originalxtype=='numberfield'&&item.items.items[2].value!=null&&item.items.items[3].value!=null&&item.items.items[2].value>item.items.items[3].value){
	    				   			showError('前面的数字不能大于后面的数字!');
	    		   					flag=false;
	    		   					return;
	    				   		}
	    				   		if(item.originalxtype=='datefield'){
	    				   			var reg = /^[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$/;
	    				   			if((item.items.items.length != 5 && item.items.items[2].rawValue.match(reg)==null) || (item.items.items.length == 5 && (item.items.items[2].rawValue.match(reg)==null || item.items.items[3].rawValue.match(reg)==null))){
	    				   				showError('输入日期有误,请重新输入!');
	    				   				flag=false;
                    					return;
	    				   			}
	    				   		}
	    				   		var it = items[index].items.items;
	    		   				for(var j=0;j<it.length-1;j++){
	    		   					if((it[j].value==""&&it[j].value!=0)||it[j].value==null){
	    		   						showError('筛选条件存在空值!');
	    		   						flag=false;
	    		   						return;
	    		   					}    		   		
	    		   				}
	    					   if(index>0) condition+=' and ';	    					
	    					   condition+=item.formatConditon();
	    					   
	    				   });
		    			   if(flag){
		    				   var columns=grid.columns;
		    				   var res = new Ext.util.MixedCollection();	    			    			
		    				   for(var i in items){
		    					   me.setHeaderEmptyText(items[i],res); 
		    		   			}
		    				   grid.fireEvent('headerfilterchange',grid,res);
		    				   Ext.Array.each(columns,function(c){    					   
		    					   Ext.Array.each(items,function(item){
		    						   if(c.dataIndex==item.items.items[0].value){
		    							   c.textEl.dom.style.fontStyle='italic';
		    							   c.textEl.dom.style.fontWeight='bold';
		    							   c.textEl.dom.style.fontSize='13px';
		    							  // c.textEl.dom.style.textDecoration='underline';
		    							   return false;
		    						   }
		    					   });
		    				   });
		    				   var baseCondition = getUrlParam('urlcondition');
		    				   if(baseCondition != null&&baseCondition!=''&&baseCondition!='null'){
		    					   condition = baseCondition + " AND " + condition;
		    				   }	    				  
		    				   grid.filterCondition = condition;
		    				   grid.defaultFilterCondition =condition;
		    				   grid.fromHeader = true;
		    				   grid.getCount(caller);
		    				   parent.Ext.getCmp('newrownumberer').fireEvent('headerfilterschange', '','',true);
		    				   parent.Ext.getCmp('searchwin').destroy();
		    				 }	 
	    			   }
	    		   },
	    		   '#saveButton':{
	    		   	   afterrender:function(btn){
	    		   	   		btn.disable();
	    		   	   },
	    			   click:function(){
	    				   var TreePanel=Ext.getCmp('TreePanel');
	    				   var caller = getUrlParam('caller');
	    				   var checked=TreePanel.getView().getSelectionModel().getSelection();
	    				   if(checked.length<1){
	    					   showError("新增查询方案请点击另存为!");
	    					   return;
	    				   }
	    				   var id=checked[0].data.id;
	    				   if(id==null||id==''||id==undefined){
	    					   showError('新增查询方案请点击另存为!');
	    					   return;
	    				   }
	    				   var dataArr = this.getData();
	    				   if(dataArr.length<1){
	    					   var treeId = Ext.getCmp('TreePanel').getSelectionModel().getLastSelected().data.id;
	    					   me.getTreeNodeData(treeId);
	    					   showError('不能保存空筛选方案!');
	    					   return;
	    				   }
	    				   var isDefalut = Ext.getCmp('setDefaultButton').getValue();
	    				   Ext.MessageBox.confirm("请确认" ,"确定保存吗?" ,function( button,text ){  
	    					   if( button == 'yes'){  
	    						   Ext.Ajax.request({
	    							   url: basePath + 'common/Datalist/querySave.action',	
	    							   params: {
	    								   id:id,
	    								   dataArr:'['+dataArr.toString()+']',
	    								   isDefalut:isDefalut,
	    								   caller:caller
	    							   },
	    							   callback: function(opt, s, r) {
	    								   var rs = Ext.decode(r.responseText);
	    								   if(rs.exceptionInfo) {
	    									   showMessage('提示', rs.exceptionInfo);
	    									   return;
	    								   } else if(rs.success) {
	    									   Ext.Msg.alert("消息","保存成功！",function(){
	    										   window.location.reload();			
	    									   });		
	    								   } 							
	    							   }
	    						   });
	    					   }  
	    				   }   
	    				   ); 
	    			   }
	    		   },
	    		   '#saveAsButton':{
	    			   click:function(){
	    				   var bool = this.isAdmin();
	    				   var me=this;
	    				   var dataArr = me.getData();
	    				   if(dataArr.length<1){
	    					   showError('不能另存为空筛选方案!');
	    					   var tree = Ext.getCmp('TreePanel').getSelectionModel().getLastSelected();
	    					   if(tree == null){
	    						   window.location.reload();
	    					   }else{
	    						   me.getTreeNodeData(tree.data.id);
	    					   }
	    					   return;
	    				   }
	    				   var win =  Ext.create('Ext.Window', {
	    					   id : 'win',
	    					   height: 150,
	    					   width: 300,
	    					   modal:true,
	    					   title:'另存为',
	    					   cls:'saveasdetail',
	    					   closable : false,
	    					   buttonAlign : 'center',
	    					   layout : 'anchor',
	    					   items : [{
	    						   fieldLabel:'方案名称',
	    						   labelWidth: 80,
	    						   width:250,
	    						   id:'queryName',
	    						   xtype:'textfield',
	    						   focusCls: 'x-form-field-cir'
	    					   },{
	    						   boxLabel:'默认方案',
	    						   labelAlign:'right',
	    						   labelWidth: 120,
	    						   width:200,
	    						   id:'defaultCheck',
	    						   checked:false,
	    						   xtype:'checkboxfield'
	    					   },{
	    						   boxLabel:'标准方案',
	    						   labelWidth: 120,
	    						   width:200,
	    						   id:'normalCheck',
	    						   checked:false,
	    						   hidden:!bool,
	    						   xtype:'checkboxfield'
	    					   }],
	    					   buttons : [{
	    						   text :'确认',
	    						   id:'certainButton',
	    						   cls: 'certainButton',
	    						   //iconCls:'sureButtonico',
	    						   handler : function(btn){
	    						   		var caller = getUrlParam('caller');
	    				   				var queryName = Ext.getCmp('queryName').getValue();
	    				   				var defaultCheck=Ext.getCmp('defaultCheck').getValue();
	    				   				var normalCheck=Ext.getCmp('normalCheck').getValue();
	    				  				queryName=Ext.String.trim(queryName);
	    				   				if(queryName==''||queryName==null||queryName==undefined){
	    									showError('请输入方案名称!');
	    									return;
	    								}
	    							   Ext.MessageBox.confirm( "请确认"  ,"确定保存吗?" ,function( button,text ){  
	    								   if( button == 'yes'){  
	    									   Ext.Ajax.request({
	    										   url: basePath + 'common/Datalist/querySaveAnother.action',	
	    										   params: {
	    											   queryName:queryName,
	    											   isDefault:defaultCheck,
	    											   isNormal:normalCheck,
	    											   dataArr:'['+dataArr.toString()+']',
	    											   caller:caller
	    										   },
	    										   callback: function(opt, s, r) {
	    											   var rs = Ext.decode(r.responseText);
	    											   if(rs.exceptionInfo) {
	    												   showMessage('提示', rs.exceptionInfo);
	    												   return;
	    											   } else if(rs.success) {
	    												   Ext.Msg.alert("消息","保存成功！",function(){
	    													   Ext.getCmp('win').close();
	    													   window.location.reload();			
	    												   });
	    												 var search=parent.document.getElementById("search");
				   										search.classList.remove("newrownum_usearch");
        		  		 								search.classList.add("newrownum_search");  
	    											   } 							
	    										   }
	    									   });
	    								   }  
	    							   }   
	    							   ); 
	    						   }
	    					   } , {
	    						   text : '取消',
	    						   id:'quashButton',
	    						   cls: 'quashButton',
	    						   //iconCls:'cancleButtonico',
	    						   handler : function(btn){
	    							   btn.ownerCt.ownerCt.close();
	    						   }
	    					   }]
	    				   });
	    				   win.show();
	    			   }
	    		   }
	    	   });
	       },
	       getData:function(){
	    	   var me=this;
	    	   var grid = Ext.getCmp('conditionpanel');
	    	   var items = grid.items.items;
	    	   var dataArr = [];
	    	   for(var i in items){
	    		   var it = items[i].items.items;
	    		   for(var j=0;j<it.length-1;j++){
	    		   		if((it[j].value==""&&it[j].value!=0)||it[j].value==null){
	    		   			showError('筛选条件存在空值!');
	    		   			return;
	    		   		}	    		   		
	    		   } 
	    		   var json = '';
	    		   var originalxtype = parent.Ext.getCmp(it[0].value+'Filter').originalxtype;
	    		   if(originalxtype=='datefield'){
	    			   if(it.length==3){
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\""+it[1].value+"\",\"value\":\"空(未填写)\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }else if(it.length==4){
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\""+it[1].value+"\",\"value\":\""+Ext.util.Format.date(it[2].value, 'Y-m-d')+"\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }else if(it.length==5){
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\"~\",\"value\":\""+Ext.util.Format.date(it[2].value, 'Y-m-d')+"~"+Ext.util.Format.date(it[3].value, 'Y-m-d')+"\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }	
	    		   }else{
	    			   if(it.length==3){
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\""+it[1].value+"\",\"value\":\"空(未填写)\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }else if(it.length==4){	    			
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\""+it[1].value+"\",\"value\":\""+it[2].value+"\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }else if(it.length==5){
	    				   json = "{\"column_value\":\""+it[0].value+"\",\"type\":\"~\",\"value\":\""+it[2].value+"~"+it[3].value+"\",\"originalxtype\":\""+originalxtype+"\"}";
	    			   }    		
	    		   }
	    		   dataArr.push(json);
	    	   }
	    	   return dataArr;
	       },
	       isAdmin:function(){
	    	   var bool = false;
	    	   Ext.Ajax.request({
	    		   url: basePath + 'common/Datalist/isAdmin.action',	
	    		   async:false, 
	    		   callback: function(opt, s, r) {
	    			   var rs = Ext.decode(r.responseText);
	    			   if(rs.exceptionInfo) {
	    				   showMessage('提示', rs.exceptionInfo);
	    				   return;
	    			   } else if(rs.success) {
	    				   bool = rs.isAdmin;
	    			   } 							
	    		   }
	    	   });
	    	   return bool;
	       },
	       parseValue:function(originalxtype,value){
	    	   if(originalxtype == 'numberfield'||originalxtype == 'datefield'){
	    		   if(value.indexOf('>=')==0){
	    			   return value.split('>=')[1];
	    		   }else if(value.indexOf('<=')==0){
	    			   return value.split('<=')[1];
	    		   }else if(value.indexOf('>')==0){
	    			   return value.split('>')[1];
	    		   }else if(value.indexOf('<')==0){
	    			   return value.split('<')[1];
	    		   }else if(value.indexOf('!=')==0){
	    			   return value.split('!=')[1];
	    		   }else if(value.indexOf('=')==0){;
	    			   var valueX = value.split('=')[1];
	    			   var length = valueX.split('-').length;
	    			   if(length<3){
	    				   if(length == 1){
	    					   var reg = /^=[0-9]{4}$/;
	    					   if(value.match(reg)==null){
	    						   showError('输入筛选数据有误,请重新输入!');
	    						   return;
	    					   }else{
	    						   var value1 = Ext.Date.toString(new Date(valueX+'-01-01'));
	    						   var value2 = Ext.Date.toString(new Date(valueX+'-12-31'));
	    						   return value =  value1 + "~"+ value2 ;}
	    				   }else if(length == 2){
	    					   var reg = /^=[0-9]{4}-((0[1-9])|(1[0-2])){1}$/;
	    					   if(value.match(reg)==null){
	    						   showError('输入筛选数据有误,请重新输入!');
	    						   return;
	    					   }else{
	    						   var day = new Date(valueX.split('-')[0],valueX.split('-')[1],0);
	    						   var value1 = Ext.Date.toString(new Date(valueX+'-01'));
	    						   var value2 = Ext.Date.toString(new Date(valueX+'-'+day.getDate()));
	    						   return value = value1 + "~"+ value2;}
	    				   }
	    			   }else{	
	    				   return value.split('=')[1];
	    			   }
	    		   }else {
	    			   return value;
	    		   }
	    	   }else{
	    		   return value;
	    	   }
	       },
	       getFilterValues:function(){
	    	   var me=this,values=[],columns=parent.Ext.getCmp(gridId).columns,v;
	    	   Ext.Array.each(columns,function(c){
	    		   if(c.hidden==false && c.width!=0 && c.dataIndex!="" && c.dataIndex){
	    			   var filtercolum= parent.Ext.getCmp(c.dataIndex+'Filter');	
	    			   if(filtercolum.emptyText!="" || filtercolum.value!=""){
	    				   if(filtercolum.originalxtype=='textfield' && filtercolum.filterType==''){
	    					   filtercolum.filterType='vague';
	    				   }else if((filtercolum.originalxtype=='datefield' || filtercolum.originalxtype=='numberfield') && filtercolum.filterType==''){
	    					   filtercolum.filterType='=';	
	    				   }
	    				   if(filtercolum.originalxtype=='combo'){
	    					   v=filtercolum.value?filtercolum.value:filtercolum.findRecordByDisplay(filtercolum.emptyText).data.value;
	    				   }else{
	    					   v=filtercolum.value?filtercolum.value:filtercolum.emptyText;
	    				   }
	    				   var q=me.parseValue(filtercolum.originalxtype,v,filtercolum);
	    				   values.push({
	    					   column_value:c.dataIndex,
	    					   value:q,
	    					   type: filtercolum.filterType,
	    					   originalxtype:filtercolum.originalxtype
	    				   });		
	    			   }	    
	    		   }
	    	   })
	    	   return values;
	       },
		parseValue:function(originalxtype,value,filtercolum){
		    if(originalxtype == 'numberfield'||originalxtype == 'datefield'){
		    		if(value.indexOf('>=')==0){
		    			filtercolum.filterType='>=';
		    			return value.split('>=')[1];
		    		}else if(value.indexOf('<=')==0){
		    			filtercolum.filterType='<=';
		    			return value.split('<=')[1];
		    		}else if(value.indexOf('>')==0){
		    			filtercolum.filterType='>';
		    			return value.split('>')[1];
		    		}else if(value.indexOf('<')==0){
		    			filtercolum.filterType='<';
		    			return value.split('<')[1];
		    		}else if(value.indexOf('!=')==0){
		    			filtercolum.filterType='!=';
		    			return value.split('!=')[1];
		    		}else if(value.indexOf('=')==0){
		    			if(originalxtype == 'numberfield'){
		    				return value.split('=')[1];
			    		}else{
			    			var valueX = value.split('=')[1];
							var length = valueX.split('-').length;
							if(length<3){
								filtercolum.filterType='~';
								if(length == 1){
									var reg = /^=[0-9]{4}$/;
									if(value.match(reg)==null){
										showError('输入筛选数据有误,请重新输入!');
										return;
									}else{
										var value1 = Ext.Date.toString(new Date(valueX+'-01-01'));
										var value2 = Ext.Date.toString(new Date(valueX+'-12-31'));
										return value =  value1 + "~"+ value2 ;}
								}else if(length == 2){
									var reg = /^=[0-9]{4}-((0[1-9])|(1[0-2])){1}$/;
									if(value.match(reg)==null){
										showError('输入筛选数据有误,请重新输入!');
										return;
									}else{
										var day = new Date(valueX.split('-')[0],valueX.split('-')[1],0);
										var value1 = Ext.Date.toString(new Date(valueX+'-01'));
										var value2 = Ext.Date.toString(new Date(valueX+'-'+day.getDate()));
										return value = value1 + "~"+ value2;}
									}
							}else{
								
								return value.split('=')[1];
							}
			    		}
		    		}else if(value.indexOf('~')>0){
		    			filtercolum.filterType='~';
		    			return value;
		    		}else {
		    			return value;
		    		}
		    	}else{
		    		return value;
		    	}
    		},
	       deleteTreeNode:function(id){
	       	 warnMsg("确定删除吗?", function(btn){
				if(btn == 'yes'){
					 Ext.Ajax.request({
	    					url: basePath + 'common/Datalist/deleteTreeNode.action',	
	    					params: {
	    						id:id
	    					},
	    					callback: function(opt, s, r) {
	    						var rs = Ext.decode(r.responseText);
	    						if(rs.exceptionInfo) {
	    							showMessage('提示', rs.exceptionInfo);
	    							return;
	    						} else if(rs.success) {
	    							Ext.Msg.alert("提示","删除成功！",function(){
	    							window.location.reload();
	    							});	
	    							parent.Ext.getCmp("newrownumberer").fireEvent("afterrender");
	    						} 							
	    					 }
	    					   });
						}
			});	            	
	       },
	       /**获取加载conditionpanel的方式*/
	       returnOpenType:function(dataIndex){
	    	   var type,columns=parent.Ext.getCmp(gridId).columns;
	    	   Ext.Array.each(columns,function(c){
	    		   if(c.hidden==false && c.width!=0 && c.dataIndex!="" && c.dataIndex){
	    			   var filtercolum= parent.Ext.getCmp(c.dataIndex+'Filter');		
	    			   if(filtercolum.value &&filtercolum.emptyText!=filtercolum.value){
	    				   type='renderOfValue';
	    				   return false;
	    			   }else if(!filtercolum.emptyText && !filtercolum.value && !type){
	    				   type='renderNoValue';
	    			   }else if(filtercolum.emptyText){
	    				   type='renderDefaultValue';
	    			   }	
	    		   }
	    	   });
	    	   return type;
	       },
	       /**获取列相关配置*/
	       getGridConf:function(){
	    	   var columns=parent.Ext.getCmp(gridId).columns,o,arr=[];
	    	   Ext.Array.forEach(columns,function(c){
	    		   if(c.hidden==false&&c.width!=0&&c.dataIndex!=""&&c.dataIndex!=undefined){    						
	    			   o={field:c.dataIndex,
	    					   text:c.text,
	    					   originalxtype:parent.Ext.getCmp(c.dataIndex+'Filter').originalxtype};	
	    			   arr.push(o);
	    		   }
	    	   });
	    	   return arr;	
	       },
	       getTreeNodeData:function(id){
	    	   var me=this;
	    	   Ext.Ajax.request({
	    		   url: basePath + 'common/Datalist/getTreeNodeData.action',		 
	    		   params: {
	    			   id:id
	    		   },
	    		   callback: function(opt, s, r) {
	    			   var rs = Ext.decode(r.responseText);
	    			   if(rs.exceptionInfo) {
	    				   showMessage('提示', rs.exceptionInfo);
	    				   return;
	    			   } else if(rs.success) {
	    				   me.drawClickTreePanel(rs);
	    				   var TreePanel=Ext.getCmp('TreePanel');
	    				   var checked=TreePanel.getView().getSelectionModel().getSelection();
	    				   var deleteButton= Ext.getCmp("deleteButton");
	    				   if(checked[0].data.ISNORM_==-1&&rs.isadmin=='normal'){
								deleteButton.disable();	
	    				   }else{
	    				   	 	deleteButton.enable();
	    				   }
	    			   } 				
	    		   }
	    	   });
	       },
	       /**渲染conditionpanel*/
	       renderConditionPanel:function(data){
	    	   var tree=Ext.getCmp('TreePanel');
	    	   this.loadTreeNode(data);
	    	/*   var openType=this.returnOpenType();
	    	   if(openType=='renderNoValue'){
	    		   this.renderNoValuePanel();
	    	   }else if(openType=='renderOfValue'){
	    		   this.renderOfValuePanel();
	    	   }else if(openType=='renderDefaultValue'){
	    		   Ext.Array.each(data,function(item){
	    			   if(item['ISDEFAULT_']==-1){
	    				   var r=tree.getStore().getNodeById(item['ID_']);
	    				   tree.getSelectionModel().select(r);
	    				   return false;
	    			   } 			
	    		   });       		
	    	   }*/	
	    	    this.renderOfValuePanel();
	       },
	       /**
	        * 筛选头有值加载panel
	        * */
	       renderOfValuePanel:function(){
	    	   var me=this, values=me.getFilterValues(),items=new Array();
	    	   var conpanel=Ext.getCmp('conditionpanel');
	    	   conpanel.removeAll();
	    	   for(var i=0;i<values.length;i++){
	    		   items.push({
	    			   xtype:'concontainer',
	    			   originalxtype:values[i].originalxtype,
	    			   conData:values[i],
	    			   FieldStore:conpanel.FieldStore
	    		   });
	    	   }
	    	   conpanel.add(items);
	       },
	       /**方案加载conditionpanel*/
	       drawClickTreePanel:function(rs){
	    	   var me=this,conpanel=Ext.getCmp('conditionpanel'),items=new Array();FILTERJSON=rs.data.FILTERJSON_;
	    	   conpanel.removeAll();
	    	   for(var i=0;i<FILTERJSON.length;i++){
	    		   items.push({
	    			   xtype:'concontainer',
	    			   originalxtype:FILTERJSON[i].originalxtype,
	    			   conData:FILTERJSON[i],
	    			   FieldStore:conpanel.FieldStore
	    		   });
	    	   }
	    	   conpanel.add(items);
	       },
	       /**空面板加载*/
	       renderNoValuePanel:function(){
	    	   var me=this;
	    	   var conpanel=Ext.getCmp('conditionpanel');
	    	   conpanel.add({
	    		   xtype:'concontainer',
	    		   FieldStore:conpanel.FieldStore
	    	   });
	       },
	       getDataListFilterName:function(fn){
	    	   var me =this;
	    	   var caller = getUrlParam('caller');
	    	   Ext.Ajax.request({
	    		   url: basePath + 'common/Datalist/getDataListFilterName.action',	
	    		   params: {
	    			   caller:caller
	    		   },
	    		   callback: function(opt, s, r) {
	    			   var rs = Ext.decode(r.responseText);
	    			   if(rs.exceptionInfo) {
	    				   showMessage('提示', rs.exceptionInfo);
	    				   return;
	    			   } else if(rs.success) {	
	    				   fn.call(me,rs.data);
	    			   } 			
	    		   }
	    	   });
	       },
	       loadTreeNode:function(data){
	    	   var TreePanel=Ext.getCmp("TreePanel");
	    	   var bool = false;
	    	   Ext.Array.forEach(data,function(d){
	    		   if(d.ISDEFAULT_==-1){
	    			   var newnode={ISNORM_:d.ISNORM_, qtip:d.NAME_,id:d.ID_,text:'<div style="display:inline"><div style="overflow:hidden;max-width:140px;width:140px;text-overflow:ellipsis;display:inline-block;">'+d.NAME_+'</div><div style="display:inline-block;overflow:hidden;color:gray;margin-left:15px">默认</div></div>',leaf:true,expanded:true};
	    		   }else{
	    			   var newnode={ ISNORM_:d.ISNORM_,qtip:d.NAME_,id:d.ID_,text:'<div style="display:inline"><div style="overflow:hidden;max-width:140px;width:140px;text-overflow:ellipsis;display:inline-block;">'+d.NAME_+'</div></div>',leaf:true,expanded:true};
	    		   }
	    		   if(d.ISNORM_==-1){
	    			   TreePanel.store.tree.root.childNodes[0].appendChild(newnode);
	    		   }else{
	    			   TreePanel.store.tree.root.childNodes[1].appendChild(newnode);
	    		   }
	    		   if(d.ISDEFAULT_==-1){
	    			   var record = TreePanel.getStore().getNodeById(d.ID_);
	    			   record.isdefault=true; 	
	    		   }
	    	   })	
	       },
	       cleanHeaderValue:function(grid){
	    	   var columns = grid.columns;
	    	   var res = new Ext.util.MixedCollection();
	    	   var numplugin = parent.Ext.getCmp('newrownumberer');
	    	   for(var i in columns){
	    		   var dataIndex = columns[i].dataIndex;
	    		   if(dataIndex&&dataIndex != ""){
	    			   var field = parent.Ext.getCmp(dataIndex+'Filter');
	    			   field.emptyText = '';
	    			   field.inputEl.dom.placeholder = '';
	    			   columns[i].textEl.dom.style.fontStyle='';
	    			   columns[i].textEl.dom.style.fontWeight='';
	    			   columns[i].textEl.dom.style.fontSize='14px';
	    			   //columns[i].textEl.dom.style.textDecoration='';
	    			   field.inputEl.dom.disabled="";
	    			   field.inputEl.dom.style.background="#eee";
	    			   if(field.originalxtype=='combo'&&field.value!=''&&field.value!=null){
	    				   field.isChange = true;
	    			   }
	    			   field.setValue('');
	    			   res.add(new Ext.util.Filter({
               			 property: dataIndex,
                		 value: "",
                		 root: 'data',
                		 label: columns[i].text||columns[i].header,
                		 type:field.originalxtype=='textfield'?numplugin.textField[field.filterType]:'',
                		 originalxtype:field.originalxtype,
                         comboValue:field.originalxtype=='combo'?field.getRawValue():''
            			}));
	    		   }
	    	   }
	    	   grid.fireEvent('headerfilterchange',grid,res);
	       },
	       setHeaderEmptyText:function(item,res){
	    	   var originalxtype=item.originalxtype,items=item.items.items,field=items[0].value,type=items[1].value,v1=items[2].value;  
	    	   var filterfield=parent.Ext.getCmp(field+'Filter');
	    	   var numplugin = parent.Ext.getCmp('newrownumberer');
	    	   switch (originalxtype) {
	    	   case 'combo':
	    		   filterfield.emptyText = items[2].getRawValue();
	    		   filterfield.inputEl.dom.placeholder = items[2].getRawValue();
	    		   break;
	    	   case 'datefield':	    		  
	    		   if(type=='~'){
	    			   v1=Ext.Date.format(new Date(v1),'Y-m-d')+'~'+Ext.Date.format(new Date(items[3].value),'Y-m-d');
	    		   }else v1=type+Ext.Date.format(new Date(v1),'Y-m-d');
	    		   filterfield.emptyText =v1;
	    		   filterfield.inputEl.dom.placeholder =v1;
	    		   break;	
	    	   case 'numberfield':
	    		   if(type=='~') v1=v1+"~"+items[3].value;
	    		   else{
	    		   		v1=type+" "+v1;
	    		   }
	    		   filterfield.emptyText =v1;
	    		   filterfield.inputEl.dom.placeholder =v1;
	    		   break; 
	    	   default:
	    		   filterfield.filterType = type;
	    	   	   filterfield.fromQuery = true;
	    	   if(type=='null'){
	    		   filterfield.emptyText = '空(未填写)';
	    		   filterfield.inputEl.dom.placeholder = '空(未填写)';
	    		   v1 = '空(未填写)';
	    	   }else {
	    		   filterfield.emptyText = v1;
	    		   filterfield.inputEl.dom.placeholder = v1;
	    	   }	    		  
	    	   break;
	    	   }
	    	   var dataIndex = item.items.items[0].value;
	    	   res.add(new Ext.util.Filter({
	                property: dataIndex,
	                value: v1,
	                root: 'data',
	                label: filterfield.fieldLabel,
	                type:originalxtype=='textfield'?numplugin.textField[filterfield.filterType]:'',
	                originalxtype:originalxtype,
	                comboValue:originalxtype=='combo'?items[2].getRawValue():''
	    	   }));
	       }
});