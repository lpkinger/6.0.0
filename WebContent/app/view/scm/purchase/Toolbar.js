	Ext.define('erp.view.scm.purchase.Toolbar',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.erpPurchaseToolbar',
		dock: 'bottom',
		initComponent : function(){ 
			Ext.apply(this,{
				items: [{
					xtype: 'button',
					itemId: 'remove',
		            text: '<font id="removeText"/>',
		            handler : this.deleteDetail
				},'->',{
					xtype: 'tbtext',
					html: '<marquee scrollamount="2">'+ $I18N.common.toolbar.prompt +'</marquee>'
				}]
			});
			this.callParent(arguments); 
		},
		deleteDetail: function(){
			var index = Number(document.getElementById('removeText').innerHTML.substring(3,4));//得到要删除的行号
			var store = Ext.getCmp('grid').store;
			var pd_puid = store.data.items[index-1].data['pd_puid'];
			if(pd_puid > 0){//这里表示该明细行为从数据库读出来的数据，删除时，要操作数据库
				var pd_id = store.data.items[index-1].data['pd_id'];
				Ext.Ajax.request({
			   		url : basePath + 'scm/purchase/deleteDetail.action',
			   		params: {
			   			pd_id: Number(pd_id)
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
			   				delSuccess(ok);//@webContent/i18n/i18n.js
			   				function ok(btn){
								if(btn=='ok'){
									store.removeAt(index-1);//删除该行
									//重新排序
									var length = store.data.length;
									for(var i=index-1;i<length;i++){
										store.data.items[i].set('pd_detno',i+1);//明细行自动编号
									}
								} else {
									return;
								}								
							};
			   			}
			   		}
				});
			} else {
				store.removeAt(index-1);//删除该行
				//重新排序
				var length = store.data.length;
				for(var i=index-1;i<length;i++){
					store.data.items[i].set('pd_detno',i+1);//明细行自动编号
				}
			}
		}
	});