Ext.define('erp.view.core.button.EdiToProdin',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEdiToProdinButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'sendEdi',
    	text: $I18N.common.button.erpEdiToProdinButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners:{
        	click:function(self){
	 			var grid = Ext.getCmp('batchDealGridPanel');
	 			var keyField = grid.keyField;
		        var items = grid.selModel.getSelection();
		        var idArr = new Array();
		        var ids ;
		        
		        Ext.each(items, function(item, index){
		        	var keyVal = item.data[keyField];
		        	if(keyVal){
		        		var bool = true;
		        		Ext.each(idArr, function(id,index){
		        			if(id == keyVal){
		        				bool = false;
		        			}
		        		});
		        		if(bool){
		        			idArr.push(keyVal);
		        		}
		        	}
		        });
		        
				if(idArr.length<=0){
        			showError('请勾选需要的明细!');
        			return;
        		}
        		ids = idArr.join(',');
        		var myMask = new Ext.LoadMask(Ext.getBody(), {
					msg    : "正在操作,请稍后...",
					msgCls : 'z-index:1000;'
				});
				myMask.show();
        		Ext.Ajax.request({
        			url:basePath + 'scm/reserve/turnEdiToProdIn.action',
        			method:'post',
        			params:{
        				ids:ids
        			},
        			callback:function(options,success,response){
        				myMask.hide();
        				var res = Ext.decode(response.responseText);
        				var html;
        				if(res.success){
        					var msg = res.msg;
        					if(msg&&msg.length>0){
        						var str = '已生成入库单';
        						var max = false;
        						Ext.Array.each(msg,function(item,index){
        							if(index>15&&!max){
        								max = true;
        							}
         							str += "<br/>"+(index+1)+".单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckin&formCondition=pi_idIS"+item.id+"&gridCondition=pd_piidIS"+item.id+"')\">" 
        								+ item.code + "</a><span>&nbsp;&nbsp;发票号:"+item.invoiceno + "</span>";       								
        						});
        						if(max){
	        						Ext.create('Ext.window.Window', {
									    title: '提示',
									    height: 350,
									    width: 300,
									    autoScroll:true,
									    layout: 'fit',
									    html:str,
									    buttons:[{
											text:'确定',
											handler:function(self){
												this.ownerCt.ownerCt.close();
											}
									    }],
									    buttonAlign:'center'
									}).show();        							
        						}else{
        							Ext.Msg.alert('提示',str);
        						}
        					}else{
        						Ext.Msg.alert('提示','无入库单生成!');
        					}
        					Ext.getCmp('dealform').onQuery(true);
        				}else if(res.exceptionInfo){
        					showError(res.exceptionInfo);
        				}
        			}
        		});
        	} 
		}
	});