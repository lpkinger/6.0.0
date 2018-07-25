/* Extending/depending on:
~ = modified function (when updating from SVN be sure to check these for changes, especially to Ext.tree.TreeNodeUI.render() )
+ = added function

TreeSelectionModel.js
    Ext.tree.CheckNodeMultiSelectionModel : ~init(), ~onNodeClick(), +extendSelection(), ~onKeyDown()

TreeNodeUI.js
    Ext.tree.CheckboxNodeUI : ~render(), +checked(), +check(), +toggleCheck()

TreePanel.js
    Ext.tree.TreePanel : +getChecked()

TreeLoader.js
    Ext.tree.TreeLoader : ~createNode()

*/

/**
 * 原始功能只能实现选中或不选两种状态
 * 但我们现在需要非叶子节点可以实现三种状态，既：
 *   节点的子节点都被选中时，显示都被选中的图标
 *   节点的子节点都没选中时，显示都没选中的图标
 *   节点的子节点部分选中时，显示部分选中的图标
 *
 *   显示都被选中，和部分选中时，当前节点的状态是被选中
 *   显示都没选中时，当前节点的状态是没选中
 *
 *   显示都被选中时，单击当前节点，触发的事件是，当前节点变成未选中状态，所有子节点变成未选中状态
 *   显示都没选中时，单击当前节点，触发的事件是，当前节点变成都选中状态，所有子节点变成选中状态
 *   显示部分选中时，单击当前节点，触发的事件是，当前节点变成都选中状态，所有子节点变成选中状态
 *
 *   子节点变成未选中状态时，对应父节点判断所有子节点是否未选中，如果都未选中，变成未选中状态，否则变成部分选中状态
 *   子节点变成选中状态时，对应父节点判断所有子节点是否都选中，如果都选中，变成都选中状态，否则变成部分选中状态
 *
 * @return {Array} 选中节点的id数组
 */
Ext.tree.TreePanel.prototype.getChecked = function(node){
    var checked = [], i;
    if( typeof node == 'undefined' ) {
        node = this.rootVisible ? this.getRootNode() : this.getRootNode().firstChild;
    }

    if( node.attributes.checked ) {
        checked.push(node.id);
        if( !node.isLeaf() ) {
            for( i = 0; i < node.childNodes.length; i++ ) {
                checked = checked.concat( this.getChecked(node.childNodes[i]) );
            }
        }
    }
    return checked;
};

/**
 * @class Ext.tree.CustomUITreeLoader
 * @extends Ext.tree.TreeLoader
 * 重写createNode()，强制uiProvider是任意的TreeNodeUI来保存广度
 */
Ext.tree.CustomUITreeLoader = function() {
    Ext.tree.CustomUITreeLoader.superclass.constructor.apply(this, arguments);
};

Ext.extend(Ext.tree.CustomUITreeLoader, Ext.tree.TreeLoader, {
    createNode : function(attr){
        Ext.apply(attr, this.baseAttr || {});

        if(this.applyLoader !== false){
            attr.loader = this;
        }

        // 如果uiProvider是字符串，那么要么从uiProviders数组里取一个对应的，要么解析字符串获得uiProvider
        if(typeof attr.uiProvider == 'string'){
            attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
        }

        // 返回的时候，如果是叶子，就返回普通TreeNode，如果不是叶子，就返回异步读取树
        return(attr.leaf ? new Ext.tree.TreeNode(attr) : new Ext.tree.AsyncTreeNode(attr));
    }
});


/**
 * @class Ext.tree.CheckboxNodeUI
 * @extends Ext.tree.TreeNodeUI
 * 给所有节点添加checkbox
 */
Ext.tree.CheckboxNodeUI = function() {
    Ext.tree.CheckboxNodeUI.superclass.constructor.apply(this, arguments);
};

Ext.extend(Ext.tree.CheckboxNodeUI, Ext.tree.TreeNodeUI, {
    /**
     * 重写renderElements
     */
    renderElements : function(n, a, targetNode, bulkRender){
        // add some indent caching, this helps performance when rendering a large tree
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        var cb = typeof a.checked == 'boolean';
        cb = true;

        var href = a.href ? a.href : Ext.isGecko ? "" : "#";
/*
        var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
            '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on" />',
            cb ? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked ? 'checked="checked" />' : '/>')) : '',
            '<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="1" ',
             a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>"].join('');
*/
        // modification，添加checkbox
        var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
            '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on" />',

            '<img src="',this.emptyIcon,'" class="',this.isAllChildrenChecked(),'" />',

            '<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="1" ',
             a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>"].join('');


        var nel;
        if(bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl())){
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
        }else{
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
        }

        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        var index = 3;
        if(cb){
            this.checkbox = cs[3]; // modification，添加checkbox
            this.checkboxImg = cs[3]; // 修改，添加仿造checkbox的图片
            index++;
            //Ext.log(this.checkbox);
            //Ext.log(index);

            // modification: 添加handlers，避免修改Ext.tree.TreeNodeUI
            Ext.fly(this.checkbox).on('click', this.check.createDelegate(this, [null]));
            n.on('dblclick', function(e) {
                if (this.isLeaf()) {
                    this.getUI().toggleCheck();
                }
            });

        }
        this.anchor = cs[index];
        this.textNode = cs[index].firstChild;

    }

    // 这个节点是否被选中了
    , checked : function() {
        // return this.checkbox.checked;
        return this.checkboxImg.className != "x-tree-node-checkbox-none";
    },

    // flag可能是：（NULL）
    // 以当前节点状态为准判断，ALL,SOME -> NONE -> ALL,SOME
    // 否则按照设置的flag为准：SOME,ALL,NONE
    check : function(forParent, forChildren) {
        var flag = null;

        var cls = this.checkboxImg.className;
        flag = (cls == "x-tree-node-checkbox-none") ? "x-tree-node-checkbox-all" : "x-tree-node-checkbox-none";
/*
        if (this.node.isLeaf) {
            flag = (cls == "x-tree-node-checkbox-all") ? "x-tree-node-checkbox-none" : "x-tree-node-checkbox-all";
        } else {
            if (cls == 'x-tree-node-checkbox-none') {
                // 全部选中
                flag = 'x-tree-node-checkbox-all';
            } else {
                // 全部反选
                flag = 'x-tree-node-checkbox-none';
            }
        }
*/

        if (typeof forParent == "undefined" || forParent == null) {
            forParent = (typeof this.node.parentNode != "undefined") && (this.node.parentNode != null);
        }
        if (typeof forChildren == "undefined" || forChildren == null) {
            forChildren = !this.node.ifLeaf;
        }
        //Ext.log(this);
        //Ext.log(flag + "," + forParent + "," + forChildren);

        var n = this.node;
        var tree = n.getOwnerTree();
        var parentNode = n.parentNode;
        // 如果下级节点都尚未渲染过，就展开当前节点，并渲染下面的所有节点
        if (!n.isLeaf && !n.expanded && !n.childrenRendered) {
            n.expand(false, false, this.check.createDelegate(this, [forParent, forChildren]));
            return;
        }

        // 如果包含子节点
        if (forChildren && !n.isLeaf) {
            var cs = n.childNodes;
            for(var i = 0; i < cs.length; i++) {
                cs[i].getUI().checkChild(flag == "x-tree-node-checkbox-all");
            }
        }

        //this.checkboxImg.className = "x-tree-node-checkbox-" + this.isAllChildrenChecked();
        this.checkboxImg.className = flag;
        if (this.checkboxImg.className == "x-tree-node-checkbox-none") {
            this.node.attributes.checked = false;
        } else {
            this.node.attributes.checked = true;
        }

        if (parentNode.getUI().checkParent) {
            parentNode.getUI().checkParent();
        }

    }

    , checkParent : function() {
        this.checkboxImg.className = "x-tree-node-checkbox-" + this.isAllChildrenChecked();
        if (this.checkboxImg.className == "x-tree-node-checkbox-none") {
            this.node.attributes.checked = false;
        } else {
            this.node.attributes.checked = true;
        }
        if (this.node.parentNode.getUI().checkParent) {
            this.node.parentNode.getUI().checkParent();
        }
    }

    , checkChild : function(flag) {
        this.node.attributes.checked = flag;
        if (!node.isLeaf) {
            node.getUI().checkChild(flag);
        }
    }

    , onDblClick : function(e){
        e.preventDefault();
        if(this.disabled){
            return;
        }
        if(this.checkbox){
            this.toggleCheck();
        }
        //if(!this.animating && this.node.hasChildNodes()){
        //    this.node.toggle();
        //}
        this.fireEvent("dblclick", this.node, e);
    }

    , toggleCheck : function(state) {
        this.check();
    }

    // 是否为叶子节点
    , isLeaf : function() {
        return this.node.attributes.leaf;
    }

    // 获得子节点
    , getChildren : function() {
        return this.node.attributes.children;
    }

    // 是否为选中状态
    , isChecked : function() {
        return this.node.attributes.checked;
    }

    // 获得className
    , getClassName : function() {
        return this.checkboxImg.className;
    }

    // 初始化时，决定显示的图片
    , isAllChildrenChecked : function() {
        if (!this.isLeaf()) {
            try{
            var status = this.alreadyCheckAllChildren(this.getChildren(), this.isChecked());
            return 'x-tree-node-checkbox-' + status;
            } catch(e){
                console.debug(e);
            }
        } else {
            return this.isChecked() ? "x-tree-node-checkbox-all" : "x-tree-node-checkbox-none";
        }
    }

    // 计算点击后，应该显示的图片
    , getNextStatus : function() {
        return this.getClassName() == 'x-tree-node-checkbox-none' ?
            'x-tree-node-checkbox-all' :
            'x-tree-node-checkbox-none';
/*
        if (this.isLeaf()) {
            return this.getClassName() == "x-tree-node-checkbox-all" ? "x-tree-node-checkbox-none" : "x-tree-node-checkbox-all";
        } else {
            if (this.getClassName() == "x-tree-node-checkbox-none") {
                return "x-tree-node-checkbox-all";
            } else {
                return "x-tree-node-checkbox-none";
            }
            if (this.getClassName() == "x-tree-node-checkbox-all") {
                return "x-tree-node-checkbox-none";
            } else if (this.getClassName() == "x-tree-node-checkbox-none") {
                return "x-tree-node-checkbox-some";
            } else {
                return "x-tree-node-checkbox-all";
            }
        }
*/
    }

    , alreadyCheckAllChildren : function(children, isChecked) {
        //console.error(children);
        //console.debug(isChecked);
        var all = 0;
        var some = 0;
        if (children) {
            for (var i = 0; i < children.length; i++) {
                var c = children[i];
                if (c.leaf) {
                    if (c.checked) {
                        all++;
                    }
                } else {
                    if (this.alreadyCheckAllChildren(c.children, c.checked) == 'all') {
                        all++;
                    } else if (this.alreadyCheckAllChildren(c.children, c.checked) == 'some') {
                        some++;
                    }
                }
            }
            if (all == children.length) {
                return 'all'
            } if (isChecked || (all + some > 0)) {
                return 'some';
            } else {
                return 'none';
            }
        } else {
            if (isChecked) {
                return 'all';
            } else {
                return 'none';
            }
        }
    }

    // 改变父节点的className
    , changeParentStatus : function() {
        var status = this.alreadyCheckAllChildren(this.getChildren());
        return 'x-tree-node-checkbox-' + status;
    }

    // 处理点击节点事件
    , check : function() {
        this.checkboxImg.className = this.getNextStatus();
        this.node.attributes.checked = "x-tree-node-checkbox-none" != this.getClassName();
        if (this.node.parentNode.getUI().checkParent) {
            this.node.parentNode.getUI().checkParent();
        }
        if (!this.isLeaf()) {
            this.node.expand(false, false);
            var children = this.node.childNodes;
            for (var i = 0; i < children.length; i++) {
                children[i].getUI().checkChild("x-tree-node-checkbox-all" == this.getClassName());
            }
        }
    }

    // 点击节点后，事件传播到父节点
    // 向父节点传播的事件可能是all, none, some
    , checkParent : function() {
        this.checkboxImg.className = this.changeParentStatus();
        this.node.attributes.checked = "x-tree-node-checkbox-none" != this.getClassName();
        if (this.node.parentNode.getUI().checkParent) {
            this.node.parentNode.getUI().checkParent();
        }
    }

    // 点击节点后，事件传播到子节点
    // 向子节点传播的事件，只有all和none两种
    , checkChild : function(isCheck) {
        if (this.isLeaf()) {
            this.checkboxImg.className = isCheck ? "x-tree-node-checkbox-all" : "x-tree-node-checkbox-none";
            this.node.attributes.checked = "x-tree-node-checkbox-none" != this.checkboxImg.className;
        } else {
            if (isCheck) {
                // 只当下面节点没选中的时候，才强制改变成some
                // 否则保持some或all状态
                if (this.checkboxImg.className == "x-tree-node-checkbox-none") {
                    this.checkboxImg.className = "x-tree-node-checkbox-all";
                }
                this.node.attributes.checked = true;
            } else {
                this.checkboxImg.className = "x-tree-node-checkbox-none";
                this.node.attributes.checked = false;
                this.node.expand(false, false);
            }
            var children = this.node.childNodes;
            for (var i = 0; i < children.length; i++) {
                children[i].getUI().checkChild("x-tree-node-checkbox-all" == this.getClassName());
            }
        }
    }

});


/**
 * @class Ext.tree.CheckNodeMultiSelectionModel
 * @extends Ext.tree.MultiSelectionModel
 * Multi selection for a TreePanel containing Ext.tree.CheckboxNodeUI.
 * Adds enhanced selection routines for selecting multiple items
 * and key processing to check/clear checkboxes.
 */
Ext.tree.CheckNodeMultiSelectionModel = function(){
   Ext.tree.CheckNodeMultiSelectionModel.superclass.constructor.call(this);
};

Ext.extend(Ext.tree.CheckNodeMultiSelectionModel, Ext.tree.MultiSelectionModel, {
    init : function(tree){
        this.tree = tree;
        tree.el.on("keydown", this.onKeyDown, this);
        tree.on("click", this.onNodeClick, this);
    },

    /**
     * Handle a node click
     * If ctrl key is down and node is selected will unselect the node.
     * If the shift key is down it will create a contiguous selection
     * (see {@link Ext.tree.CheckNodeMultiSelectionModel#extendSelection} for the limitations)
     */
    onNodeClick : function(node, e){
        if( e.shiftKey && this.extendSelection(node) ) {
            return true;
        }
        if( e.ctrlKey && this.isSelected(node) ) {
            this.unselect(node);
        } else {
            this.select(node, e, e.ctrlKey);
        }
    },

    /**
     * Selects all nodes between the previously selected node and the one that the user has just selected.
     * Will not span multiple depths, so only children of the same parent will be selected.
     */
    extendSelection : function(node) {
        var last = this.lastSelNode;
        if( node == last || !last ) {
            return false; /* same selection, process normally normally */
        }

        if( node.parentNode == last.parentNode ) {
            var cs = node.parentNode.childNodes;
            var i = 0, attr='id', selecting=false, lastSelect=false;
            this.clearSelections(true);
            for( i = 0; i < cs.length; i++ ) {
                // We have to traverse the entire tree b/c don't know of a way to find
                // a numerical representation of a nodes position in a tree.
                if( cs[i].attributes[attr] == last.attributes[attr] || cs[i].attributes[attr] == node.attributes[attr] ) {
                    // lastSelect ensures that we select the final node in the list
                    lastSelect = selecting;
                    selecting = !selecting;
                }
                if( selecting || lastSelect ) {
                    this.select(cs[i], null, true);
                    // if we're selecting the last node break to avoid traversing the entire tree
                    if( lastSelect ) {
                        break;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
});
