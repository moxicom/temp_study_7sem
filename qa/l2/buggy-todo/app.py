from flask import Flask, render_template, request, redirect, url_for, jsonify

app = Flask(__name__)

# == In-memory "DB" ==
tasks = []
_next_id = 1

def _gen_id():
    global _next_id
    v = _next_id
    _next_id += 1
    return v

@app.route('/')
def index():
    # Renders list of tasks
    return render_template('index.html', tasks=tasks)

@app.route('/add', methods=['POST'])
def add():
    """Добавление задачи. (BUG: допускает пустые названия)"""
    title = request.form.get('title', '')
    # intentionally allow empty title (bug)
    task = {'id': _gen_id(), 'title': title.strip(), 'done': False}
    tasks.append(task)
    return redirect(url_for('index'))

@app.route('/toggle/<int:task_id>', methods=['POST'])
def toggle(task_id):
    """
    Toggle done. (BUG: uses list index instead of matching by 'id')
    Example bug outcome: toggling wrong task or 404 for large id.
    """
    try:
        # BUG: treat task_id as index into tasks list
        tasks[task_id]['done'] = not tasks[task_id]['done']
        return ('', 204)
    except Exception:
        return ('Not Found', 404)

@app.route('/delete/<int:task_id>', methods=['POST'])
def delete(task_id):
    """
    Delete. (BUG: deletes by matching title == str(task_id) instead of id)
    Also returns 200 even if nothing deleted (should be 404).
    """
    sid = str(task_id)
    for t in tasks:
        if t['title'] == sid:
            tasks.remove(t)
            return ('', 204)
    # BUG: returns 200 instead of 404 -> client assumes success
    return ('', 200)

@app.route('/edit/<int:task_id>', methods=['POST'])
def edit(task_id):
    """
    Edit a task. (BUG: appends a new task instead of updating existing one)
    """
    title = request.form.get('title', '')
    # BUG: create new task instead of editing
    task = {'id': _gen_id(), 'title': title.strip(), 'done': False}
    tasks.append(task)
    return redirect(url_for('index'))

# Simple API endpoints (for debugging)
@app.route('/api/tasks', methods=['GET'])
def api_list():
    return jsonify(tasks)

if __name__ == '__main__':
    # Run dev server
    app.run(debug=True, host='127.0.0.1', port=5000) 