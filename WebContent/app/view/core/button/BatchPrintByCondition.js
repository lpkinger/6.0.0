/**
 * 批处理界面按条件打印按钮
 */	
Ext.define('erp.view.core.button.BatchPrintByCondition',{ 
		id:'batchprintbycondition',
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchPrintByConditionButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	printType:'',
    	text: $I18N.common.button.erpBatchPrintByConditionButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
    	jasperReportPrint:function(reportname){  
    		var me=this;
			var form=this.ownerCt.ownerCt, grid = form.ownerCt.down('grid');
    		var keyField = form.fo_keyField;
    		var items = grid.getMultiSelected();
    		if (items.length>1000){
    			showMessage('提示', '勾选行数必须小于1000条!');
				return;
    		}
    		var ids =new Array();
    		var length = items.length;
    		var idStr ='';
    		Ext.each(items,function(item,index){
    			if(length!=index+1){
    				idStr = idStr+item.data[keyField]+',';
    			}else{
    				idStr = idStr+item.data[keyField];
    			}
    		});
	    	if(idStr.length==0){
		  		showError('未勾选任何明细');
		  	}else{
			  	form.setLoading(true);
			    Ext.Ajax.request({
			    	url : basePath +'common/JasperReportPrint/batchPrint.action',
					params: {
						ids:idStr,
						caller:caller,
						reportname:reportname
					},
					method : 'post',
					timeout: 360000,
					callback : function(options,success,response){
						form.setLoading(false);
						var res = new Ext.decode(response.responseText);
						if(res.success){ 							
							me.openWindowWithPost(res.info.printurl,res.info.userName,res.info.reportName,
										res.info.whereCondition,res.info.printtype,encodeURIComponent(res.info.title)); //改用post方式 解决where条件过长的问题
						}else if(res.exceptionInfo){
							var str = res.exceptionInfo;
							showError(str);return;
						}
					}
			    });
		  	}
    	},
    	openWindowWithPost : function (url,userName,reportName,whereCondition,printType,titlename){
		  var newWindow = window.open(url, '_blank');  
  	      if (!newWindow)  
  	          return false;   
  	      var html = "";  
  	      html += "<html><head></head><body><form id='batchprintpostwin' method='post' action='" + url + "'>";   	      
  	      html += "<input type='hidden' name='userName' value='" + userName + "'/>"; 
  	      html += "<input type='hidden' name='reportName' value='" + reportName + "'/>"; 
  	      html += "<input type='hidden' name='whereCondition' value='" + whereCondition + "'/>"; 
  	      html += "<input type='hidden' name='printType' value='" + printType + "'/>"; 
  	      html += "<input type='hidden' name='title' value='" + titlename + "'/>"; 
  	      html += "</form><script type='text/javascript'>document.getElementById('batchprintpostwin').submit();";  
  	      html += "<\/script></body></html>".toString().replace(/^.+?\*|\\(?=\/)|\*.+?$/gi, "");   
  	      newWindow.document.write(html);    	        
  	      return newWindow; 
  	  },
	  initComponent : function(){ 
			this.getMenu();
			this.callParent(arguments); 
		},
		getMenu:function(){
			var me=this;
			Ext.Ajax.request({
	  			url: basePath + 'common/JasperReportPrint/getFields.action',
	   			method: 'post',
	   			params:{
		   			caller:caller
		   		},
		 		callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText); 
					var menu =Ext.create('Ext.menu.Menu',{});
                    menu.removeAll();
                    for(var i=0;i<rs.datas.length;i++){
                        menu.add({
                           id: rs.datas[i].ID,
                           text: rs.datas[i].TITLE,
                           name:rs.datas[i].REPORTNAME,
                           iconCls: 'x-button-icon-print',
                           handler:function(){
                           		me.jasperReportPrint(this.name);
						   }
                        });	
                    }
                    me.menu = menu;
				}
	    	});
		}
	});