Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AttentionMain', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.attention.AttentionMain','oa.attention.AttentionMainGrid','oa.attention.Form','core.form.ColorField','core.button.Save','core.button.Close','oa.attention.AttentionManageGrid',
    		'core.form.ScopeField','core.grid.Panel2','core.trigger.MultiDbfindTrigger','oa.addrBook.AddrBookTree','oa.attention.AttentionMainTreePanel','common.datalist.Toolbar','core.form.PhotoField'
    	
    	],
    init:function(){
      var me=this;
    	this.control({ 
    	  'AttentionMainTreePanel':{
    	    afterrender:function(panel){
	    	   var button=new Object();
	    	    button.xtype='button';
	    	    button.cls='btn-cls';
	    	    button.text='查看所有';
	    		button.iconCls='x-button-icon-addgroup';
	    	    button.style='margin-left:20px;';
	    	    button.handler=function open(){
	    			me.addGroup();
	    		};
	    	    panel.add(button);
    	    },
    	     itemmousedown:function(view,record ){
    	       var grid=Ext.getCmp('grid');
    	       var data=record.data;
    	       var value='';
    	       if(!data.leaf){
    	       value=data.qtip;
    	       }else value=data.id;
    	      if(record.data.parentId=='root'){
    	        if(grid.isHidden()){
    	          grid.show();
    	        }
    	      }else {
    	       me.createForm(value);
    	      // me.createGrid();
    	       grid.hide();
    	      }
    	     }
    	  }	,
    	  'erpAttentionMainGridPanel':{
    	   afterrender:function(panel){
    	   }    	    
    	  },
    	  'button[id=accredit]':{
    	    click:function(){
    	     me.accreditAttention();
    	    }
    	  },
    	  'button[id=delete]':{
    	    click:function(){
    	     me.Delete();
    	    }
    	  },
    	  'button[id=rank]':{
    	    click:function(){
    	      me.Rank();
    	    }    	  
    	  }
    	});
    },
    AddAttention:function(){
      var selectedemid=null;
      var condition='';
      if(selectedemid){
        condition="ap_attentedemid="+selectedemid+" AND ap_emid="+emid;
      }else condition= "ap_emid="+0;
       var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'关注项设置',
					width : '65%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionSub.jsp?urlcondition='+condition+'&caller=AttentionPerson" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
    },
    Rank:function(){
        var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'等级设置',
					width : '60%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionGrade.jsp'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();   
    },
    accreditAttention:function(){
      var selectedemid=null;
      var condition='';
      if(selectedemid){
        condition="ap_accreditedemid="+selectedemid+" AND aa_emid="+emid;
      }else condition= "aa_emid="+0;
        var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'授权设置',
					width : '65%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionSub.jsp?urlcondition='+condition+'&caller=AccreditAttention" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
    
    },
    Delete:function(){
     var grid=Ext.getCmp('AttentionGridPanel');
     var data=grid.getMultiSelected();
     var param=new Object();
     param.data=data;
     Ext.Ajax.request({//拿到form的items
        	url : basePath + 'oa/attention/deleteAttentions.action',
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        	  var res = new Ext.decode(response.responseText);
        	  if(res.success){
        	   saveSuccess(
        	   grid.loadNewStore(grid,{caller:caller,condition: "ap_emid="+emid})
        	   );
        	  }
         }
       });
    },
  createForm:function(value){
    var me =  this;
	var data=null;
	Ext.Ajax.request({
		url : basePath + 'oa/attention/getAttentionEmployeeByParam.action',
		params: {
			caller: 'AttentionEmployee',
			param:value
		},
		method : 'post',
		async: false,
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				showError(res.exceptionInfo);
				return;
			}
			data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
		}
	});
	var form=Ext.getCmp('form')
	if(form){
	  form.destroy();
	}
	var keyValue=data.em_id;
	var keyField='ap_attentedemid';
	var url='jsps/oa/attention/AttentionManageDetail.jsp';
	var title='详细信息';
	var panel= Ext.create('Ext.form.Panel', {
		title: '<font color=green>关注人查看 >>'+data.em_name+'</font>',
		bodyPadding: 5,
		iconCls: 'main-activeuser',
		layout: 'column',
		id:'form',
		defaults: {
			anchor: '100%',
			readOnly:true,
			columnWidth:0.5,
			fieldStyle : 'background:#f0f0f0;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none;font-weight: bold; ',
		},
		frame:true,
		defaultType: 'textfield',
		items: [{
			html:'<div style="font-weight:bold; font-size:15px">基本信息</div>',
			columnWidth:1
		},{
			fieldLabel: '姓名',
			name: 'em_name',
			id:'em_name',
		},{
			fieldLabel: '编号',
			name: 'em_code',
		},{
			fieldLabel:'照片',
		    fieldStyle:'margin-right:0',
			name:'em_photourl',
			xtype:'photofield',
			value:data.em_photourl,
		},{
			fieldLabel:'性别',
			name:'em_sex',
		},{
			fieldLabel:'出生年月',
			name:'em_birthday'
		},{
			fieldLabel:'联系电话',
			name:'em_tel',
		},{
			fieldLabel: '移动电话',
			name: 'em_mobile',
		},{
			fieldLabel: '默认邮箱',
			name: 'em_email',
		},{
			fieldLabel: 'UU号',
			name: 'em_uu',
		},{
			fieldLabel:'籍贯',
			name:'em_native'
		},{
			fieldLabel:'工作年限',
			name:'em_worktime',
		},{
			xtype:'button',
			layout:'fit',
			columnWidth:0,
			iconCls:'x-button-icon-paging',
			listeners:{
				click:function(btn,e){
					var othername=Ext.getCmp('em_name').getValue();
					showDialogBox(e,null,emid,othername);
				}
			}
		},{
			xtype:'button',
			layout:'fit',
			columnWidth:0,
			iconCls:'x-button-icon-paging',
			listeners:{
				click:function(btn,e){
					console.log(data);
					if(data.em_uu!=null){
						var jid =data.em_uu+"@58.61.153.82";
						var url = 'uas:'+jid;
						window.location = url;
					
					}
				}
			}
		},{
			html:'<div style="font-weight:bold; font-size:15px">组织信息</div>',
			columnWidth:1
		},{
			fieldLabel:'所属组织',
			name:'em_worktime',
		},{
			fieldLabel:'所属部门',
			name:'em_birthday'
		},{
			fieldLabel:'部门负责人',
			name:'or_headmanname'
		},{
		  html:'<a style="text-decoration: none||blink;font-size:16px;text-align:left;font-weight: bold; " href="javascript:openUrl(' + keyValue + ',\''+keyField+'\',\''+url+'\',\''+title+'\''+ ');">查看详细</a>',
		
		}],
		renderTo: Ext.get('employeedata'),
		listeners:{
			afterrender:function(btn,e){
				Ext.getCmp('photobutton').hide();
			}
		}
	});	
    panel.getForm().setValues(data);
    var gridfields="";
    var griddata="";
    var gridcolumns="";
    Ext.Ajax.request({
		url : basePath + 'oa/attention/getDataAndColumnsByParam.action',
		params: {
			caller: 'AttentionManageDetail',
			param:value
		},
		method : 'post',
		async: false,
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				showError(res.exceptionInfo);
				return;
			}
		 	griddata = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
		    gridfields=res.fields;
		    gridcolumns=res.columns;
		}
	 });
	 var store=Ext.create('Ext.data.Store',{
        fields:gridfields,
        data:griddata
       });
	 if(Ext.getCmp('detaigrid')){   
	   Ext.getCmp('detaigrid').getView().bindStore(store);
       return;
	 }
	var grid= Ext.create('Ext.grid.Panel', {
	 id:'detaigrid',
      store: Ext.create('Ext.data.Store',{
        fields:gridfields,
        data:griddata,
     }),
     autoScroll:true,
     columnLines:true,
     title: '<font color=green>关注明细</font>',
     height:window.innerHeight*0.35,
     layout:'auto',
     columns:gridcolumns,
     renderTo: Ext.get('details'),
     });
    },
    showDialogBox: function (e,id, otherId, other, date, context){
    	var panel = Ext.getCmp('dialog-win-' + otherId);
    	if(!panel){
    		panel = Ext.create('erp.view.core.window.DialogBox', {
    			other: other,
    			autoShow: false,
    			otherId: otherId
    		});
    		panel.showAt(e.getXY());
    	}
    	if(!Ext.isEmpty(id)){
    		panel.insertDialogItem(other, date, context);
    		if(Ext.getCmp('dialog-min-' + otherId)){
    			Ext.getCmp('dialog-min-' + otherId).setText("<font color=red>有新消息...</font>" );
    		} else {
    			updatePagingStatus(id, 1);
    		}
    	}
    },
     mergeCells:function(grid,cols){  
    var arrayTr=document.getElementById(grid.getId()+"-body").firstChild.firstChild.firstChild.getElementsByTagName('tr');    
    var trCount = arrayTr.length;  
    var arrayTd;  
    var td;  
    var merge = function(rowspanObj,removeObjs){ //定义合并函数  
        if(rowspanObj.rowspan != 1){  
            arrayTd =arrayTr[rowspanObj.tr].getElementsByTagName("td"); //合并行  
            td=arrayTd[rowspanObj.td-1];  
            td.rowSpan=rowspanObj.rowspan;  
            td.vAlign="middle";
            td.style="text-align:center";               
            Ext.each(removeObjs,function(obj){ //隐身被合并的单元格  
                arrayTd =arrayTr[obj.tr].getElementsByTagName("td");  
                arrayTd[obj.td-1].style.display='none';                           
            });  
        }     
    };    
    var rowspanObj = {}; //要进行跨列操作的td对象{tr:1,td:2,rowspan:5}      
    var removeObjs = []; //要进行删除的td对象[{tr:2,td:2},{tr:3,td:2}]  
    var col;  
    Ext.each(cols,function(colIndex){ //逐列去操作tr  
        var rowspan = 1;  
        var divHtml = null;         
        for(var i=1;i<trCount;i++){
            arrayTd = arrayTr[i].getElementsByTagName("td");  
            var cold=0;  
//          Ext.each(arrayTd,function(Td){ //获取RowNumber列和check列  
//              if(Td.getAttribute("class").indexOf("x-grid-cell-special") != -1)  
//                  cold++;                               
//          });  
            col=colIndex+cold;//跳过RowNumber列和check列  
            if(!divHtml){  
                divHtml = arrayTd[col-1].innerHTML;  
                rowspanObj = {tr:i,td:col,rowspan:rowspan}  
            }else{  
                var cellText = arrayTd[col-1].innerHTML;  
                var addf=function(){   
                    rowspanObj["rowspan"] = rowspanObj["rowspan"]+1;  
                    removeObjs.push({tr:i,td:col});  
                    if(i==trCount-1)  
                        merge(rowspanObj,removeObjs);
                };  
                var mergef=function(){  
                    merge(rowspanObj,removeObjs);
                    divHtml = cellText;  
                    rowspanObj = {tr:i,td:col,rowspan:rowspan}  
                    removeObjs = [];  
                };  
                if(cellText == divHtml){  
                    if(colIndex!=cols[0]){   
                        var leftDisplay=arrayTd[col-2].style.display;
                        if(leftDisplay=='none')  
                            addf();   
                        else  
                            mergef();                             
                    }else  
                        addf();                                           
                }else  
                    mergef();             
            }  
        }  
    });   
}  
});