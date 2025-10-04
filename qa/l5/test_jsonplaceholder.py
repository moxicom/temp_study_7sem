import pytest
import requests

BASE_URL = "https://jsonplaceholder.typicode.com/posts"


# ---------- GET ----------
def test_get_posts():
    response = requests.get(BASE_URL)

    assert response.status_code == 200, f"Unexpected status: {response.status_code}"

    json_data = response.json()
    assert isinstance(json_data, list), "Response is not an array"

    for post in json_data:
        assert set(post.keys()) == {"userId", "id", "title", "body"}, f"Unexpected keys: {post.keys()}"
        assert isinstance(post["userId"], int)
        assert isinstance(post["id"], int)
        assert isinstance(post["title"], str)
        assert isinstance(post["body"], str)


# ---------- POST ----------
def test_post_create():
    payload = {
        "userId": 10,
        "title": "title #1",
        "body": "new  post"
    }

    response = requests.post(BASE_URL, json=payload)

    assert response.status_code == 201, f"Unexpected status: {response.status_code}"

    json_data = response.json()
    assert isinstance(json_data, dict), "Response is not a JSON object"
    assert json_data, "Response body is empty"

    assert set(json_data.keys()) == {"userId", "title", "body", "id"}, f"Unexpected keys: {json_data.keys()}"

    assert json_data["title"] == payload["title"]
    assert json_data["body"] == payload["body"]

    assert isinstance(json_data["id"], int)


def test_put_update():
    payload = {
        "title": "upd",
        "body": "upd"
    }

    response = requests.put(f"{BASE_URL}/1", json=payload)

    assert response.status_code == 200, f"Unexpected status: {response.status_code}"

    json_data = response.json()
    assert isinstance(json_data, dict), "Response is not a JSON object"
    assert json_data, "Response body is empty"

    assert set(json_data.keys()) == {"id", "title", "body"}, f"Unexpected keys: {json_data.keys()}"

    assert json_data["id"] == 1
    assert json_data["title"] == "upd"
    assert json_data["body"] == "upd"

    assert isinstance(json_data["id"], int)
