/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://www.extjs.com/license
 *
 * @author Lingo
 * @since 2007-09-21
 * http://code.google.com/p/anewssystem/
 */
/**
 * 声明Ext.lingo命名控件
 * TODO: 完全照抄，作用不明
 */
Ext.namespace("Ext.lingo");
/**
 * 下拉树形.
 *
 */
//
//    var tree = new Ext.tree.TreePanel(el,{containerScroll: true});
//    var p = new Ext.data.HttpProxy({url:url});
//    p.on("loadexception", function(o, response, e) {
//        if (e) throw e;
//    });
//    p.load(null, {
//        read: function(response) {
//            var doc = response.responseXML;
//            tree.setRootNode(rpTreeNodeFromXml(doc.documentElement || doc));
//        }
//    }, callback || tree.render, tree);
//    return tree;
//
//}


// 从xml中创建树
/*
function rpTreeNodeFromXml(XmlEl) {
    // 如果是text节点，就取节点值，如果不是text节点，就取tagName
    var t = ((XmlEl.nodeType == 3) ? XmlEl.nodeValue : XmlEl.tagName);

    // 没有text，没有节点的时候
    if (t.replace(/\s/g,'').length == 0) {
        return null;
    }

    // 特别指定一个没有任何属性，并且只包含一个text节点的元素
    var leafTextNode = ((XmlEl.attributes.length == 0) && (XmlEl.childNodes.length == 1) && (XmlEl.firstChild.nodeType == 3));
    if (leafTextNode ) {
        return new Ext.tree.TreeNode({
            tagName: XmlEl.tagName,
            text: XmlEl.firstChild.nodeValue
        });
    }

    var result = new Ext.tree.TreeNode({
        text : t
    });
    result.tagName=XmlEl.tagName;

    // 为元素处理属性和子节点
    if (XmlEl.nodeType == 1) {
        Ext.each(XmlEl.attributes, function(a) {
            result.attributes[a.nodeName]=a.nodeValue;
            if(a.nodeName=='text')
              result.setText(a.nodeValue);
        });
        if (!leafTextNode) {
            Ext.each(XmlEl.childNodes, function(el) {
                // 只处理元素和text节点
                if ((el.nodeType == 1) || (el.nodeType == 3)) {
                    var c = rpTreeNodeFromXml(el);
                    if (c) {
                        result.appendChild(c);
                    }
                }
            });
        }
    }
    return result;
}
*/

Ext.lingo.TreeField = function(config) {
    config.readOnly = true;
    Ext.lingo.TreeField.superclass.constructor.call(this, config);
    this.url = this.treeConfig.dataTag;
};
Ext.extend(Ext.lingo.TreeField, Ext.form.TriggerField, {
    triggerClass      : 'x-form-date-trigger',
    defaultAutoCreate : {
        tag          : "input",
        type         : "text",
        size         : "10",
        autocomplete : "off"
    },

    onRender : function(ct, position) {
        Ext.lingo.TreeField.superclass.onRender.call(this, ct, position);

        var hiddenName = this.treeConfig.hiddenName;
        var hiddenId = this.treeConfig.hiddenId ? this.treeConfig.hiddenId : hiddenName;
        this.treeConfig.hiddenId = hiddenId;
        this.hiddenField = this.el.insertSibling({
            tag: 'input',
            type:'hidden',
            id: hiddenId,
            name: hiddenName
        }, 'before', true);
        this.el.dom.removeAttribute('name');
    },

    getValue : function() {
        //return Ext.lingo.TreeField.superclass.getValue.call(this) || this.hiddenField.value;
        return this.hiddenField.value;
    },

    setValue : function(val) {
        if (this.hiddenField) {
            this.hiddenField.value = val;
        }
        Ext.lingo.TreeField.superclass.setValue.call(this, val);
    },

    menuListeners : {
        select:function (item, picker, node) {
            var v = node.text;
            //var v = node.id;
            var ed = this.ed;
            if (this.selfunc) {
                v = this.selfunc(node);
            }
            if (ed) {
                var r = ed.record;
                r.set(this.fn, v);
            } else {
                this.focus();
                this.selectedId = node.id;
                //this.setValue(v);
                //document.getElementById(this.treeConfig.hiddenId).value = node.id;
            }
        },

        hide : function(){
        }
    },

    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.TreeMenu(this.treeConfig);
        }

        this.menu.picker.url = this.url;
        Ext.apply(this.menu.picker,  {
            url : this.url
        });
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope : this
        }));
        this.menu.picker.setValue(this.getValue());
        this.menu.show(this.el, "tl-bl?");
    },

    getName: function() {
        return this.name;
    },

    invalidText : "{0} is not a valid date - it must be in the format {1}"
});


Ext.menu.TreeMenu = function(config) {
    this.url = config.dataTag;

    Ext.menu.TreeMenu.superclass.constructor.call(this, config);
    this.plain = true;

    var di = new Ext.menu.TreeItem(config);

    this.add(di);
    this.picker = di.picker;
    this.relayEvents(di, ["select"]);
    this.relayEvents(di, ["beforesubmit"]);
};

Ext.extend(Ext.menu.TreeMenu, Ext.menu.Menu);

Ext.menu.TreeItem = function(config){

    var treePicker = new Ext.TreePicker(config);

    this.component = treePicker;
    Ext.menu.TreeItem.superclass.constructor.call(this, treePicker, config);

    this.picker = this.component;

    this.addEvents({select: true});

    this.picker.on("render", function(picker){
        picker.getEl().swallowEvent("click");
        //picker.container.addClass("x-menu-date-item");
    });

    this.picker.on("select", this.onSelect, this);
};

Ext.extend(Ext.menu.TreeItem, Ext.menu.Adapter, {
    onSelect : function(picker, node) {
        this.fireEvent("select", this, picker, node);
        Ext.menu.TreeItem.superclass.handleClick.call(this);
    }
});

Ext.TreePicker = function(config){
    Ext.TreePicker.superclass.constructor.call(this, config);
    this.addEvents({select: true});
    if(this.handler){
        this.on("select", this.handler,  this.scope || this);
    }
};

Ext.extend(Ext.TreePicker, Ext.Component, {
    setValue : function(value) {
        this.value = value;
        if(this.tree)
            this.tree.selectPath(value, 'text');
    },

    getValue : function() {
        return this.value;
    },
    onRender : function(container) {

        var me = this;
        var dh = Ext.DomHelper;
        var el = document.createElement("div");
        el.className = "x-date-picker";
        el.innerHTML = '';
        var eo = Ext.get(el);
        this.el = eo;
        container.dom.appendChild(el);

        var tree = this.createTree(el, me.dataTag, function() {
            var tree = this;
            tree.render();
            tree.selectPath(me.getValue(), 'text');
        });
        tree.on('click',function(node,e){
            me.fireEvent("select", me, node);
        });
        this.tree = tree;
    },

    createTree : function(el, url, callback) {

        var Tree = Ext.tree;
        // id
        var tree = new Tree.TreePanel({
            el:el,
            autoScroll:true,
            animate:true,
            enableDD:true,
            autoHeight:true,
            autoWidth:true,
            containerScroll:true,
            loader: new Tree.TreeLoader({
                dataUrl: url
            })
        });

        // 设置根节点
        var root = new Tree.AsyncTreeNode({
            text      : this.initialConfig.title, // c.title
            draggable : false,
            id        : '-1'
        });
        tree.setRootNode(root);
        // 渲染
        tree.render();
        root.expand();

        return tree;
    }
});

Ext.reg('treefield', Ext.lingo.TreeField);

