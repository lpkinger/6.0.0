Ext.define('Ext.ux.form.SearchField', {
			extend : 'Ext.form.field.Trigger',

			alias : 'widget.searchfield',

			trigger1Cls : Ext.baseCSSPrefix + 'form-clear-trigger',

			trigger2Cls : Ext.baseCSSPrefix + 'form-search-trigger',

			hasSearch : false,
			paramName : 'query',

			initComponent : function() {
				this.callParent(arguments);
				this.on('specialkey', function(f, e) {
							if (e.getKey() == e.ENTER) {
								this.onTrigger2Click();
							}
						}, this);
			},

			afterRender : function() {
				this.callParent();
				this.triggerEl.item(0).setDisplayed('none');
			},

			onTrigger1Click : function() {
				var me = this, store = me.store, proxy = store.getProxy();
				if (me.hasSearch) {
					me.setValue('');
					proxy.extraParams[me.paramName] = '';
					proxy.extraParams.start = 0;
					store.load();
					me.hasSearch = false;
					me.triggerEl.item(0).setDisplayed('none');
					me.doComponentLayout();
				}
			},

			onTrigger2Click : function() {
				var me = this, store = me.store, value = me
						.getValue();
				if (value.length < 1) {
					me.onTrigger1Click();
					return;
				}

				var rootNode = store.getRootNode();
				var length = rootNode.childNodes.length;
				var k=0;
				//var h=0;
				for (var i = 0; i < length; i++) {
					var id = rootNode.childNodes[0+k].get('id');
					var subRootNode = store.getNodeById(id);
					var len = subRootNode.childNodes.length;
					var h=0;
					for (var j = 0; j < len; j++) {
						//var h=0;
						var text = subRootNode.childNodes[0+h].get('text');
						var subId = subRootNode.childNodes[0+h].get('id');
						var subChildNode = store.getNodeById(subId);
						if ((text.indexOf(value)) == -1) {
							subRootNode.removeChild(subChildNode, false);
						}else{
							h=h+1; 
						}
						//if()
						
						// if(subRootNode.childNodes.length<1){
						// rootNode.removeChild(subRootNode,false);
						//         			  
						// }
						// else{}
						// if(subRootNode.childNodes==null){
						// rootNode.removeChild(subRootNode,false);
						//         		
						// }
                    }
                     if(subRootNode.childNodes.length<1){
							 rootNode.removeChild(subRootNode,false);
							}else{
								k=k+1;
							
							}

				}
//				for (var h = 1; h < rootNode.length; h++) {
//					var id2 = rootNode.childNodes[h].get('id');
//					var subRootNode = store.getNodeById(id2);
//					if (subRootNode.childNodes.length < 1) {
//						rootNode.removeChild(subRootNode, false);
//					}
//				}
				
				me.hasSearch = true;
				me.triggerEl.item(0).setDisplayed('block');
				me.doComponentLayout();
			}
		});
